package io.nuls.api.server.business;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.*;
import io.nuls.api.server.dao.mapper.BalanceMapper;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.WebwalletUtxoLevelDbService;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: 资产
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Service
public class BalanceBusiness implements BaseService<Balance, Long> {
    @Autowired
    private BlockBusiness blockBusiness;

    @Autowired
    private BalanceMapper balanceMapper;

    @Autowired
    private WebwalletTransactionBusiness webwalletTransactionBusiness;


    private UtxoLevelDbService utxoLevelDbService = UtxoLevelDbService.getInstance();
    private WebwalletUtxoLevelDbService webwalletUtxoLevelDbService = WebwalletUtxoLevelDbService.getInstance();

    /**
     * 查询账户资产
     *
     * @param address 用户账户
     * @return
     */
    public Balance getBalance(String address) {
        Balance balance = new Balance();
        Long usable = 0L;
        Long locked = 0L;
        BlockHeader blockHeader = blockBusiness.getNewest();
        long bestHeight = 0L;
        if (blockHeader != null) {
            bestHeight = blockHeader.getHeight();
        }
        if(bestHeight < 0){
            return new Balance(address,0L,0L);
        }
        Set<String> keyList = UtxoContext.get(address);
        if(keyList.size() == 0){
            return new Balance(address,0L,0L);
        }
        List<Utxo> utxoList = utxoLevelDbService.selectList(keyList);
        //还需要过滤数据库中所有未确认交易的input列表中的utxo
        List<WebwalletTransaction> webwalletTransactionList = webwalletTransactionBusiness.getAll(address, EntityConstant.WEBWALLET_STATUS_NOTCONFIRM,0);
        Set<String> lockedUtxo = new HashSet<>();
        for(WebwalletTransaction tx: webwalletTransactionList){
            //未确认交易只有四种类型，只把锁定加入冻结
            if(tx.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS){
                for(Utxo utxo:tx.getOutputs()){
                    if(utxo.getAddress().equals(address) && utxo.getLockTime()==-1){
                        locked+= utxo.getAmount();
                    }
                }
            }
            for(Input input: tx.getInputs()){
                lockedUtxo.add(input.getKey());
            }
        }
        for (Utxo utxo : utxoList) {
            if(lockedUtxo.contains(utxo.getKey())){
                continue;
            }
            if(utxo.usable(bestHeight)){
                usable += utxo.getAmount();
            }else{
                locked += utxo.getAmount();
            }
        }
        Utxo temp = webwalletUtxoLevelDbService.select(address);
        if(null != temp) {
            usable += temp.getAmount();
            for (Utxo utxo : utxoList) {
                if(utxo.getKey().equals(temp.getKey())){
                    //删除转账时临时产生的utxo
                    webwalletUtxoLevelDbService.delete(utxo.getAddress());
                    usable -= temp.getAmount();
                    break;
                }
            }
        }
        balance.setAddress(address);
        balance.setUsable(usable);
        balance.setLocked(locked);
        return balance;
    }

    /**
     * 修改资产
     *
     * @param id     锁定金额
     * @param locked 锁定金额
     * @param usable 可用金额
     * @return 1操作成功，2id不存在，0修改失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(Long id, long locked, long usable) {
        Balance entity = getByKey(id);
        if (null == entity) {
            return 2;
        }
        entity.setId(id);
        if (locked > 0) {
            entity.setLocked(locked);
        }
        if (usable > 0) {
            entity.setUsable(usable);
        }
        return balanceMapper.updateByPrimaryKey(entity);
    }

    /**
     * 修改账户资产
     *
     * @param balance
     * @return 1成功，0对象为空，2 主键为空，3高度错误，4地址错误，5锁定余额小于0,6可用余额小于0,7资产名称为空，8资产id错误
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(Balance balance) {
        if (null == balance) {
            return 0;
        }
        if (balance.getId() < 0) {
            return 2;
        }
        if (balance.getBlockHeight() < 0) {
            return 3;
        }
        if (!StringUtils.validAddress(balance.getAddress())) {
            return 4;
        }
        if (balance.getLocked() < 0) {
            return 5;
        }
        if (balance.getUsable() < 0) {
            return 6;
        }
        if (StringUtils.isBlank(balance.getAssetsCode())) {
            return 7;
        }
        if (null == getByKey(balance.getId())) {
            return 8;
        }
        return balanceMapper.updateByPrimaryKey(balance);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKey(Long aLong) {
        return balanceMapper.deleteByPrimaryKey(aLong);
    }

    @Override
    public Balance getByKey(Long aLong) {
        return balanceMapper.selectByPrimaryKey(aLong);
    }

    /**
     * 新增资产
     *
     * @param balance
     * @return 1新增成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(Balance balance) {
        return balanceMapper.insert(balance);
    }
}
