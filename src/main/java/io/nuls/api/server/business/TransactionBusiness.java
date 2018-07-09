package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.mapper.TransactionMapper;
import io.nuls.api.server.dao.mapper.leveldb.TransactionLevelDbService;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.ArraysTool;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 交易
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class TransactionBusiness implements BaseService<Transaction, Long> {

    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private TransactionRelationBusiness relationBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;
    @Autowired
    private DepositBusiness depositBusiness;
    @Autowired
    private UtxoBusiness utxoBusiness;

    private TransactionLevelDbService transactionLevelDbService = TransactionLevelDbService.getInstance();

    /**
     * 交易列表
     * @param orderType 1 tx_index asc,2 create_time desc,3 block_height desc
     * @param height 所属的区块
     * @param type   交易类型
     * @return
     */
    public PageInfo<Transaction> getList(Long height, int type, int pageNumber, int pageSize,int orderType) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (null != height) {
            searchable.addCondition("block_height", SearchOperator.eq, height);
        }
        if (type > 0) {
            //todo 待优化
            //searchable.addCondition("type", SearchOperator.eq, type);
        }
        PageHelper.orderBy("block_height desc");
        //todo 需要联合leveldb查询list
        List<Transaction> transactionList = transactionMapper.selectList(searchable);
        formatTransaction(transactionList);
        PageInfo<Transaction> page = new PageInfo<>(transactionList);
        return page;
    }

    public List<Transaction> getList(Long blockHeight) {
        Searchable searchable = new Searchable();
        if(null == blockHeight){
            return null;
        }
        searchable.addCondition("block_height", SearchOperator.eq, blockHeight);
        PageHelper.orderBy("tx_index asc");
        List<Transaction> transactionList = transactionMapper.selectList(searchable);
        formatTransaction(transactionList);
        return transactionList;
    }

    /**
     * 查询某个地址的交易列表
     *
     * @param address 地址
     * @return
     */
    public PageInfo<Transaction> getListByAddress(String address, int type, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if(!StringUtils.validAddress(address)){
            return null;
        }
        searchable.addCondition("address", SearchOperator.eq, address);
        if (type > 0) {
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        PageHelper.orderBy("block_height desc");
        List<Transaction> transactionList = transactionMapper.selectListByAddress(searchable);
        formatTransaction(transactionList);
        PageInfo<Transaction> page = new PageInfo<>(transactionList);
        return page;
    }

    /**
     * 新增
     *
     * @param tx
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(Transaction tx) {
        transactionLevelDbService.insert(tx);
        return transactionMapper.insert(tx);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(List<Transaction> list){
        /*for(Transaction tx:list){
            transactionMapper.insert(tx);
        }*/

        if(list.size() > 0){
            if(list.size()>1000) {
                int count = list.size()%1000;
                List<List<Transaction>> lists = ArraysTool.avgList(list, count);
                for(int i = 0; i<count; i++){
                    transactionMapper.insertByBatch(lists.get(i));
                }
            }else{
                transactionMapper.insertByBatch(list);
            }
            //存入leveldb
            transactionLevelDbService.insertList(list);

        }
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollback(Transaction tx) throws Exception {
        tx.transferExtend();
        //查询出交易生成的utxo，回滚缓存时使用
        List<Utxo> utxoList = utxoBusiness.getList(tx.getHash());
        tx.setOutputs(utxoList);
        //回滚交易新生成的utxo
        //todo 这里，可以直接拿到hash和index，进行主键删除
        utxoBusiness.deleteByTxHash(tx.getHash());
        //回滚未花费输出
        utxoBusiness.rollBackByFrom(tx);
        //删除关系表
        relationBusiness.deleteByTxHash(tx.getHash());
        //根据交易类型回滚其他表数据
        if (tx.getType() == EntityConstant.TX_TYPE_REGISTER_AGENT) {
            agentNodeBusiness.deleteByKey(tx.getHash());
        }else if(tx.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS) {
            depositBusiness.deleteByKey(tx.getHash());
        }else if(tx.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT) {
            depositBusiness.rollbackCancelDeposit(tx.getHash());
        }else if(tx.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
            agentNodeBusiness.rollbackStopAgent(tx.getHash());
        }
        deleteByHash(tx.getHash());
    }

    /**
     * 根据tx删除
     *
     * @param hash hash
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByHash(String hash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("hash", SearchOperator.eq, hash);
        transactionLevelDbService.delete(hash);
        return transactionMapper.deleteBySearchable(searchable);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(Transaction transaction) {
        transactionLevelDbService.insert(transaction);
        return transactionMapper.updateByPrimaryKey(transaction);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(Long id) {
        Transaction tx = transactionMapper.selectByPrimaryKey(id);
        if(null != tx){
            transactionLevelDbService.delete(tx.getHash());
            return transactionMapper.deleteByPrimaryKey(id);
        }
        return 0;
    }

    @Override
    public Transaction getByKey(Long id) {
        Transaction tx = transactionMapper.selectByPrimaryKey(id);
        if(null != tx){
            return transactionLevelDbService.select(tx.getHash());
        }
        return null;
    }

    public Transaction getByHash(String hash) {
        return transactionLevelDbService.select(hash);
    }

    private List<Transaction> formatTransaction(List<Transaction> transactionList){
        for (Transaction trans:transactionList) {
            try {
                trans.transferExtend();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transactionList;
    }
}
