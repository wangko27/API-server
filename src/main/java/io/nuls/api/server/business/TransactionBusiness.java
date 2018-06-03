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
import io.nuls.api.utils.StringUtils;
import org.glassfish.grizzly.compression.lzma.impl.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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


    /**
     * 交易列表
     *
     * @param height 所属的区块
     * @param type   交易类型
     * @return
     */
    public PageInfo<Transaction> getList(Long height, int type, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (height >= 0) {
            searchable.addCondition("block_height", SearchOperator.eq, height);
        }
        if (type > 0) {
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        PageInfo<Transaction> page = new PageInfo<>(transactionMapper.selectList(searchable));
        return page;
    }

    /**
     * 查询某个地址的交易列表
     *
     * @param address 地址
     * @return
     */
    public PageInfo<Transaction> getListByAddress(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        //todo
        PageInfo<Transaction> page = new PageInfo<>(transactionMapper.selectList(searchable));
        return page;
    }

    /**
     * 根据交易hash查交易详情
     *
     * @param hash 交易hash
     * @return
     */
    public Transaction getTransactionDetail(String hash) {
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(hash)) {
            searchable.addCondition("hash", SearchOperator.eq, hash);
        }
        return transactionMapper.selectBySearchable(searchable);
    }

    /**
     * 新增
     *
     * @param tx
     */
    @Transactional
    public void insert(Transaction tx) {
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
        } else if (tx.getType() == EntityConstant.TX_TYPE_RED_PUNISH ||
                tx.getType() == EntityConstant.TX_TYPE_YELLOW_PUNISH) {
            punishLogBusiness.insert((PunishLog) tx.getTxData());
        }
    }

    /**
     * 根据主键删除
     *
     * @param hash 主键
     * @return 1成功，其他失败
     */
    @Transactional
    public int deleteById(String hash) {
        return transactionMapper.deleteByPrimaryKey(hash);
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
    public int save(Transaction transaction) {
        return transactionMapper.insert(transaction);
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
