package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.*;
import io.nuls.api.server.dao.mapper.AgentNodeMapper;
import io.nuls.api.server.dao.mapper.TransactionMapper;
import io.nuls.api.server.dao.mapper.TransactionRelationMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.StringUtils;
import org.glassfish.grizzly.compression.lzma.impl.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param height 所属的区块
     * @param type   交易类型
     * @return
     */
    public PageInfo<Transaction> getList(Long height, int type, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (null != height) {
            searchable.addCondition("block_height", SearchOperator.eq, height);
        }
        if (type > 0) {
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        PageHelper.orderBy("tx_index asc");
        PageInfo<Transaction> page = new PageInfo<>(transactionMapper.selectList(searchable));
        return page;
    }

    public List<Transaction> getList(Long blockHeight) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, blockHeight);
        PageHelper.orderBy("tx_index asc");
        return transactionMapper.selectList(searchable);
    }

    /**
     * 查询某个地址的交易列表
     *
     * @param address 地址
     * @return
     */
    public PageInfo<Transaction> getListByAddress(String address,int type, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        if(type > 0){
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        PageInfo<Transaction> page = new PageInfo<>(transactionMapper.selectList(searchable));
        return page;
    }

    /**
     * 新增
     *
     * @param tx
     */
    @Transactional
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
            depositBusiness.delete((Deposit) tx.getTxData());
        } else if (tx.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
            AgentNode agentNode = (AgentNode) tx.getTxData();
            agentNodeBusiness.deleteByKey(agentNode.getTxHash());
        } else if (tx.getType() == EntityConstant.TX_TYPE_RED_PUNISH) {
            punishLogBusiness.save((PunishLog) tx.getTxData());
        } else if (tx.getType() == EntityConstant.TX_TYPE_YELLOW_PUNISH) {
            punishLogBusiness.saveList(tx.getTxDataList());
        }
        return 1;
    }

    @Transactional
    public void rollback(Transaction tx) {
        //todo
    }

    /**
     * 根据高度删除
     *
     * @param height 高度
     * @return
     */
    @Transactional
    public int deleteByHeight(Long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return transactionMapper.deleteBySearchable(searchable);
    }

    @Transactional
    @Override
    public int update(Transaction transaction) {
        return transactionMapper.updateByPrimaryKey(transaction);
    }

    @Transactional
    @Override
    public int deleteByKey(String s) {
        return transactionMapper.deleteByPrimaryKey(s);
    }

    @Transactional
    @Override
    public Transaction getByKey(String s) {
        return transactionMapper.selectByPrimaryKey(s);
    }
}
