package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.TransactionRelation;
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
    private TransactionRelationBusiness relationBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;
    @Autowired
    private DepositBusiness depositBusiness;
    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private TransactionLevelDbService transactionLevelDbService;

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
        PageHelper.orderBy("block_height desc");
        List<Transaction> transactionList = transactionMapper.selectList(searchable);
        //加载list，加载leveldb中的真实交易数据
        formatTransaction(transactionList);
        PageInfo<Transaction> page = new PageInfo<>(transactionList);
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
        for (String txHash : header.getTxHashList()) {
            tx = transactionLevelDbService.select(txHash);
            if (tx != null) {
                txList.add(tx);
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
    //todo 分页的pageNumber需要修改成long，因为按照目前进度，一天8000块，一块1000笔交易，那么一年就是24亿比交易
    public PageInfo<Transaction> getListByAddress(String address, int type, int pageNumber, int pageSize) {
        PageInfo<TransactionRelation> relationPageInfo = transactionRelationBusiness.getListByPage(address, pageNumber, pageSize);
        List<TransactionRelation> relationList = relationPageInfo.getList();
        List<Transaction> transactionList = new ArrayList<>();
        if (null != relationList) {
            for (TransactionRelation relation : relationList) {
                transactionList.add(transactionLevelDbService.select(relation.getTxHash()));
            }
        }
        formatTransactionExtend(transactionList);
        PageInfo<Transaction> page = new PageInfo<>(transactionList);
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
        long time1, time2;
        if (list.size() > 0) {
            time1 = System.currentTimeMillis();
            if (list.size() > 1000) {
                int count = list.size() % 1000;
                List<List<Transaction>> lists = ArraysTool.avgList(list, count);
                for (int i = 0; i < count; i++) {
                    transactionMapper.insertByBatch(lists.get(i));
                }
            } else {
                transactionMapper.insertByBatch(list);
            }
            time2 = System.currentTimeMillis();
            if (time2 - time1 > 1000) {
                System.out.println("----------save tx mysql:" + (time2 - time1));
            }

            //存入leveldb
            time1 = System.currentTimeMillis();
            transactionLevelDbService.insertList(list);
            time2 = System.currentTimeMillis();
            if (time2 - time1 > 1000) {
                System.out.println("----------save tx mysql:" + (time2 - time1));
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollback(Transaction tx) throws Exception {
        //删除关系表
        relationBusiness.deleteByTxHash(tx.getHash());
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
//        deleteByHash(tx.getHash());
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
        transactionMapper.deleteList(txHashList);
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
        for (Transaction tx : transactionList) {
            //去leveldb中重新加载trans
            tx = transactionLevelDbService.select(tx.getHash());
        }
        return transactionList;
    }

    private List<Transaction> formatTransactionExtend(List<Transaction> transactionList) {
        return transactionList;
    }

}
