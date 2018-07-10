package io.nuls.api.server.business;

import io.nuls.api.constant.Constant;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.*;
import io.nuls.api.exception.NulsException;
import io.nuls.api.server.dao.mapper.BalanceMapper;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.resources.impl.BlockResource;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.TimeService;
import io.nuls.api.utils.log.Log;
import org.spongycastle.util.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 资产
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class BalanceBusiness implements BaseService<Balance, Long> {
    @Autowired
    private BlockBusiness blockBusiness;

    @Autowired
    private BalanceMapper balanceMapper;

    @Autowired
    private UtxoLevelDbService utxoLevelDbService;

    /**
     * 查询账户资产
     *
     * @param address 用户账户
     * @return
     */
    public Balance getBalance(String address) {
        BlockHeader blockHeader = blockBusiness.getNewest();
        long currentTime = TimeService.currentTimeMillis();
        long bestHeight = 0L;
        if (blockHeader != null) {
            bestHeight = blockHeader.getHeight();
        }
        Balance balance = new Balance();
        Long usable = 0L;
        Long locked = 0L;

        List<String> keyList = UtxoContext.get(address);
        List<Utxo> utxoList = utxoLevelDbService.selectList(keyList);


        for (Utxo utxo : utxoList) {
            if (utxo.getLockTime() == -1) {
                locked += utxo.getAmount();
            } else if (utxo.getLockTime() == 0) {
                usable += utxo.getAmount();
            } else {
                if (utxo.getLockTime() >= Constant.BlOCKHEIGHT_TIME_DIVIDE) {
                    //根据时间锁定
                    if (utxo.getLockTime() <= currentTime) {
                        usable += utxo.getAmount();
                    } else {
                        locked += utxo.getAmount();
                    }
                } else {
                    //根据高度锁定
                    if (utxo.getLockTime() <= bestHeight) {
                        usable += utxo.getAmount();
                    } else {
                        locked += utxo.getAmount();
                    }
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
