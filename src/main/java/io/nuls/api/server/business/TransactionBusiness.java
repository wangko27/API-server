package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.*;
import io.nuls.api.server.dao.mapper.TransactionMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 交易
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class TransactionBusiness implements BaseService<Transaction, String> {

    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private TransactionRelationBusiness relationBusiness;
    @Autowired
    private AliasBusiness aliasBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;
    @Autowired
    private DepositBusiness depositBusiness;
    @Autowired
    private PunishLogBusiness punishLogBusiness;
    @Autowired
    private AddressRewardDetailBusiness rewardDetailBusiness;
    @Autowired
    private UtxoBusiness utxoBusiness;

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
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        if(orderType == 2) {
            PageHelper.orderBy("create_time desc");
        }else if(orderType == 3){
            PageHelper.orderBy("block_height desc");
        }else {
            PageHelper.orderBy("tx_index asc");
        }
        List<Transaction> transactionList = transactionMapper.selectList(searchable);
        formatTransaction(transactionList);
        PageInfo<Transaction> page = new PageInfo<>(transactionList);
        return page;
    }

    /**
     * 交易列表 不分页
     * @param orderType 1 tx_index asc,2 create_time desc,3 block_height desc
     * @param height 所属的区块
     * @param type   交易类型
     * @return
     */
    public List<Transaction> getListAll(Long height, int type, int pageNumber, int pageSize,int orderType) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (null != height) {
            searchable.addCondition("block_height", SearchOperator.eq, height);
        }
        if (type > 0) {
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        if(orderType == 2) {
            PageHelper.orderBy("create_time desc");
        }else if(orderType == 3){
            PageHelper.orderBy("block_height desc");
        }else {
            PageHelper.orderBy("tx_index asc");
        }
        return transactionMapper.selectList(searchable);
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
        transactionMapper.insert(tx);
        relationBusiness.saveTxRelation(tx);
        if (tx.getType() == EntityConstant.TX_TYPE_COINBASE) {
            rewardDetailBusiness.saveTxReward(tx);
        } else if (tx.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS) {
            aliasBusiness.save((Alias) tx.getTxData());
        } else if (tx.getType() == EntityConstant.TX_TYPE_REGISTER_AGENT) {
            agentNodeBusiness.save((AgentNode) tx.getTxData());
        } else if (tx.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS) {
            depositBusiness.save((Deposit) tx.getTxData());
        } else if (tx.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT) {
            depositBusiness.cancelDeposit((Deposit) tx.getTxData(), tx.getHash());
        } else if (tx.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
            AgentNode agentNode = (AgentNode) tx.getTxData();
            agentNodeBusiness.stopAgent(agentNode, tx.getHash());
        } else if (tx.getType() == EntityConstant.TX_TYPE_RED_PUNISH) {
            punishLogBusiness.save((PunishLog) tx.getTxData());
        } else if (tx.getType() == EntityConstant.TX_TYPE_YELLOW_PUNISH) {
            punishLogBusiness.saveList(tx.getTxDataList());
        }
        return 1;
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollback(Transaction tx) throws Exception {
        tx.transferExtend();
        //查询出交易生成的utxo，回滚缓存时使用
        List<Utxo> utxoList = utxoBusiness.getList(tx.getHash());
        tx.setOutputs(utxoList);
        //回滚交易新生成的utxo
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
        transactionMapper.deleteByPrimaryKey(tx.getHash());
    }

    /**
     * 根据高度删除
     *
     * @param height 高度
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByHeight(Long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return transactionMapper.deleteBySearchable(searchable);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(Transaction transaction) {
        return transactionMapper.updateByPrimaryKey(transaction);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(String s) {
        return transactionMapper.deleteByPrimaryKey(s);
    }

    @Override
    public Transaction getByKey(String s) {
        return transactionMapper.selectByPrimaryKey(s);
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
