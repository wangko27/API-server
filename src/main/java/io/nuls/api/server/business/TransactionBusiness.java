package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.*;
import io.nuls.api.server.dao.mapper.TransactionMapper;
import io.nuls.api.server.dao.mapper.leveldb.TransactionLevelDbService;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.ArraysTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private TransactionRelationBusiness transactionRelationBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;
    @Autowired
    private DepositBusiness depositBusiness;
    @Autowired
    private UtxoBusiness utxoBusiness;

    private TransactionLevelDbService transactionLevelDbService = TransactionLevelDbService.getInstance();

    public List<Transaction> getIndexTransaction(){
        List<Transaction> list = IndexContext.getTransactions();
        return formatTransaction(list);
    }

    /**
     * 交易列表
     *
     * @param orderType 1 tx_index asc,2 create_time desc,3 block_height desc
     * @param height    所属的区块
     * @param type      交易类型
     * @return
     */
    public PageInfo<Transaction> getList(Long height, int type, int pageNumber, int pageSize, int orderType) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (null != height) {
            searchable.addCondition("block_height", SearchOperator.eq, height);
        }
        PageHelper.orderBy("id desc");
        //List<Transaction> transactionList = transactionMapper.selectList(searchable);
        //加载list，加载leveldb中的真实交易数据
        PageInfo<Transaction> page = new PageInfo<>(transactionMapper.selectList(searchable));
        page.setList(formatTransaction(page.getList()));
        return page;
    }

    public PageInfo<Transaction> getNewestList(int pageSize) {
        PageHelper.startPage(1, pageSize);
        Searchable searchable = new Searchable();
        PageHelper.orderBy("id desc");
        List<Transaction> transactionList = transactionMapper.selectList(searchable);
        //加载list，加载leveldb中的真实交易数据
        formatTransaction(transactionList);
        PageInfo<Transaction> page = new PageInfo<>(transactionList);
        return page;
    }

    /**
     * 根据高度查询全部交易
     *
     * @param header 高度
     * @return
     */
    public List<Transaction> getList(BlockHeader header) {
        List<Transaction> txList = new ArrayList<>();
        Transaction tx;
        if(null != header.getTxHashList()){
            for (String txHash : header.getTxHashList()) {
                tx = transactionLevelDbService.select(txHash);
                if (tx != null) {
                    txList.add(tx);
                }
            }
        }

        return txList;
    }

    /**
     * 查询某个地址的交易列表
     * 先从utxo中查询出hash，然后再利用hash去leveldb去加载真实的交易数据
     *
     * @param address 地址
     * @return
     */
    //todo 分页的pageNumber需要修改成long，因为按照目前进度，一天8000块，一块1000笔交易，那么一年就是24亿笔交易
    public PageInfo<Transaction> getListByAddress(String address, int type,long startTime,long endTime, int pageNumber, int pageSize) {
        List<Transaction> transactionList = new ArrayList<>();
        PageHelper.startPage(pageNumber, pageSize);
        PageInfo<TransactionRelation> transactionRelationPageInfo = transactionRelationBusiness.getListByPage(address,type,startTime,endTime,pageNumber,pageSize);
        for (TransactionRelation relation : transactionRelationPageInfo.getList()) {
            Transaction tx = transactionLevelDbService.select(relation.getTxHash());
            tx.caclTx(tx,address);
            tx.setTxData(null);
            tx.setTxDataList(null);
            tx.setScriptSign(null);
            transactionList.add(tx);
        }
        PageInfo<Transaction> page = new PageInfo<>(transactionList);
        page.setPageNum(transactionRelationPageInfo.getPageNum());
        page.setSize(transactionRelationPageInfo.getSize());
        page.setTotal(transactionRelationPageInfo.getTotal());
        page.setStartRow(transactionRelationPageInfo.getStartRow());
        page.setEndRow(transactionRelationPageInfo.getEndRow());
        page.setPages(transactionRelationPageInfo.getPages());
        return page;
    }

    /**
     * 新增
     *
     * @param tx
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(Transaction tx) {
        transactionLevelDbService.insert(tx);
        return transactionMapper.insert(tx);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(List<Transaction> list) {
        /*for(Transaction tx:list){
            transactionMapper.insert(tx);
        }*/
        if (list.size() > 0) {
            if (list.size() > 1000) {
                int count = list.size() % 1000;
                List<List<Transaction>> lists = ArraysTool.avgList(list, count);
                for (int i = 0; i < count; i++) {
                    transactionMapper.insertByBatch(lists.get(i));
                }
            } else if (list.size() < 10) {
                for (int i = 0; i < list.size(); i++) {
                    transactionMapper.insert(list.get(i));
                }
            } else {
                transactionMapper.insertByBatch(list);
            }

            transactionLevelDbService.insertList(list);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollback(Transaction tx) throws Exception {
        //根据交易类型回滚其他表数据
        if (tx.getType() == EntityConstant.TX_TYPE_REGISTER_AGENT) {
            agentNodeBusiness.deleteByKey(tx.getHash());
        } else if (tx.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS) {
            depositBusiness.deleteByKey(tx.getHash());
        } else if (tx.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT) {
            depositBusiness.rollbackCancelDeposit(tx.getHash());
        } else if (tx.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
            agentNodeBusiness.rollbackStopAgent(tx.getHash());
        } else if (tx.getType() == EntityConstant.TX_TYPE_RED_PUNISH) {
            agentNodeBusiness.rollbackStopAgent(tx.getHash());
        }
    }

    /**
     * 根据tx删除
     *
     * @param hash hash
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByHash(String hash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("hash", SearchOperator.eq, hash);
        transactionLevelDbService.delete(hash);
        return transactionMapper.deleteBySearchable(searchable);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(Transaction transaction) {
        transactionLevelDbService.insert(transaction);
        return transactionMapper.updateByPrimaryKey(transaction);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(Long id) {
        Transaction tx = transactionMapper.selectByPrimaryKey(id);
        if (null != tx) {
            transactionLevelDbService.delete(tx.getHash());
            return transactionMapper.deleteByPrimaryKey(id);
        }
        return 0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteList(List<String> txHashList) {
        if(null != txHashList){
            transactionMapper.deleteList(txHashList);
        }
    }


    public void deleteLevelDBList(List<String> txHashList) {
        for (String txHash : txHashList) {
            transactionLevelDbService.delete(txHash);
        }
    }

    @Override
    public Transaction getByKey(Long id) {
        Transaction tx = transactionMapper.selectByPrimaryKey(id);
        if (null != tx) {
            tx = transactionLevelDbService.select(tx.getHash());
            return tx;
        }
        return null;
    }

    public Transaction getByHash(String hash) {
        return transactionLevelDbService.select(hash);
    }

    private List<Transaction> formatTransaction(List<Transaction> transactionList) {
        List<Transaction> txList = new ArrayList<>();
        for (Transaction trans : transactionList) {
            //去leveldb中重新加载trans
            trans = transactionLevelDbService.select(trans.getHash());
            //trans.setExtend(null);
            trans.setOutputs(null);
            trans.setTxData(null);
            /*trans.setScriptSign(null);*/
            //去掉TxDataList
            if (null == trans.getOutputList()) {
                trans.setOutputList(new ArrayList<>());
            }
            if (null == trans.getInputs()) {
                trans.setInputs(new ArrayList<>());
            } else {
                for (Input input : trans.getInputs()) {
                    Utxo utxo = utxoBusiness.getByKey(input.getFromHash(), input.getFromIndex());
                    input.setAddress(utxo.getAddress());
                    input.setValue(utxo.getAmount());
                }
            }
            trans.setTxDataList(null);
            txList.add(trans);
        }
        return txList;
    }

    public Transaction formatTransForDetail(Transaction transaction) {
        /*trans.setScriptSign(null);*/
        //去掉TxDataList
        if (null == transaction.getInputs()) {
            transaction.setInputs(new ArrayList<>());
        } else {
            for (Input input : transaction.getInputs()) {
                Utxo utxo = utxoBusiness.getByKey(input.getFromHash(), input.getFromIndex());
                input.setAddress(utxo.getAddress());
                input.setValue(utxo.getAmount());
            }
        }
        transaction.setTxData(null);
        transaction.setScriptSign(null);
        transaction.setTxDataList(null);
        return transaction;
    }

    public int selectTotalCount(){
        return transactionMapper.selectTotalCount();
    }
}
