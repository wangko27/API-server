package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.PunishLog;
import io.nuls.api.entity.Transaction;
import io.nuls.api.server.dao.mapper.TransactionMapper;
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
public class TransactionBusiness implements BaseService<Transaction,String> {

    @Autowired
    private TransactionMapper transactionMapper;

    /**
     * 交易列表
     * @param height  所属的区块
     * @param type 交易类型
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
        PageInfo<Transaction> page = new PageInfo<>(transactionMapper.selectList(searchable));
        return page;
    }

    /**
     * 查询某个地址的交易列表
     * @param address 地址
     * @return
     */
    public PageInfo<Transaction> getListByAddress(String address,int type, int pageNumber, int pageSize) {
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
     * @return 1成功，其他失败
     */
    @Transactional
    public void insert(Transaction tx) {
        transactionMapper.insert(tx);
        if (tx.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS) {

//            aliasBusiness.
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
    public int deleteBykey(String s) {
        return transactionMapper.deleteByPrimaryKey(s);
    }

    @Transactional
    @Override
    public Transaction getByKey(String s) {
        return transactionMapper.selectByPrimaryKey(s);
    }
}
