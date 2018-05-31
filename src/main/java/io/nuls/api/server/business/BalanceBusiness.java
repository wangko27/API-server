package io.nuls.api.server.business;

import io.nuls.api.entity.Balance;
import io.nuls.api.server.dao.mapper.BalanceMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 资产
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class BalanceBusiness {

    @Autowired
    private BalanceMapper balanceMapper;

    /**
     * 查询账户资产列表
     * @param address 用户账户
     * @return
     */
    public List<Balance> getList(String address) {
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(address)){
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        return balanceMapper.selectList(searchable);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public Balance getDetail(Long id) {
        return balanceMapper.selectByPrimaryKey(id.toString());
    }

    /**
     * 新增资产
     * @param entity
     * @return 1新增成功，其他失败
     */
    public int insert(Balance entity){
        return balanceMapper.insert(entity);
    }

    /**
     * 修改资产
     * @param address 地址
     * @param locked 锁定金额
     * @param usable 可用金额
     * @return 1操作成功，2id不存在，0修改失败
     */
    public int update(Long id,String address,long locked,long usable){
        Balance entity = getDetail(id);
        if(null == entity){
            return 2;
        }
        entity.setId(id);
        if(locked > 0){
            entity.setLocked(locked);
        }
        if(usable > 0){
            entity.setUsable(usable);
        }
        return balanceMapper.updateByPrimaryKey(entity);
    }

}
