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

}
