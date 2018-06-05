package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.Input;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.Utxo;
import io.nuls.api.entity.UtxoKey;
import io.nuls.api.exception.NulsException;
import io.nuls.api.server.dao.mapper.UtxoMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: UTXO
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class UtxoBusiness implements BaseService<Utxo, UtxoKey> {
    @Autowired
    private UtxoMapper utxoMapper;

    /**
     * 获取列表
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<Utxo> getList(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        PageInfo<Utxo> page = new PageInfo<>(utxoMapper.selectList(searchable));
        return page;
    }


    /**
     * 根据地址获取该地址全部的utxo
     *
     * @param address
     * @return
     */
    public List<Utxo> getList(String address) {

        Searchable searchable = new Searchable();
        if (StringUtils.validAddress(address)) {
            searchable.addCondition("address", SearchOperator.eq, address);
        } else {
            return null;
        }
        return utxoMapper.selectList(searchable);
    }

    /**
     * 根据主键获取详情
     *
     * @param txHash
     * @param txIndex
     * @return
     */
    public Utxo getByKey(String txHash, int txIndex) {
        UtxoKey key = new UtxoKey(txHash, txIndex);
        return utxoMapper.selectByPrimaryKey(key);
    }

    /**
     * 新增
     *
     * @param entity
     * @return 1操作成功，其他失败
     */
    @Transactional
    public int save(Utxo entity) {
        return utxoMapper.insert(entity);
    }


    @Transactional
    public int update(Utxo entity) {
        return utxoMapper.updateByPrimaryKey(entity);
    }

    @Transactional
    @Override
    public int deleteByKey(UtxoKey utxoKey) {
        return utxoMapper.deleteByPrimaryKey(utxoKey);
    }

    @Override
    public Utxo getByKey(UtxoKey utxoKey) {
        return utxoMapper.selectByPrimaryKey(utxoKey);
    }


    /**
     * 根据主键删除
     *
     * @param txHash
     * @return
     */
    @Transactional
    public int delete(String txHash, Integer index) {
        UtxoKey key = new UtxoKey(txHash, index);
        return utxoMapper.deleteByPrimaryKey(key);
    }

    @Transactional
    public void deleteByTxHash(String txHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, txHash);
        utxoMapper.deleteBySearchable(searchable);
    }

    /**
     * 根据每一条交易的输入，改变对应的utxo状态
     *
     * @param tx
     * @return
     */
    @Transactional
    public void updateByFrom(Transaction tx) {
        //coinBase交易，红黄牌交易没有inputs
        if (tx.getInputs() == null) {
            return;
        }

        UtxoKey key = new UtxoKey();
        Utxo utxo;
        for (Input input : tx.getInputs()) {
            key.setTxHash(input.getFromHash());
            key.setTxIndex(input.getFromIndex());
            utxo = utxoMapper.selectByPrimaryKey(key);

            utxo.setSpendTxHash(tx.getHash());

            //在这里查询出utxo后，记得给每一个input赋值address

            input.setAddress(utxo.getAddress());
            input.setValue(utxo.getAmount());
            utxoMapper.updateByPrimaryKey(utxo);
        }
    }

    @Transactional
    public void rollBackByFrom(Transaction tx) {
        //删除当前交易生成的utxo
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, tx.getHash());
        utxoMapper.deleteBySearchable(searchable);

        //回滚每条被花费的输出
        UtxoKey utxoKey;
        for (Input input : tx.getInputs()) {
            utxoKey = new UtxoKey(input.getFromHash(), input.getFromIndex());
            Utxo utxo = utxoMapper.selectByPrimaryKey(utxoKey);
            utxo.setSpendTxHash(null);
            utxoMapper.updateByPrimaryKey(utxo);
        }
    }

    @Transactional
    public void saveTo(Transaction tx) {
        for (Utxo utxo : tx.getOutputs()) {
            utxoMapper.insert(utxo);
        }
    }

}
