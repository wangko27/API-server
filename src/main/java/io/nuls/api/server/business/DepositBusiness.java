package io.nuls.api.server.business;

import io.nuls.api.entity.Deposit;
import io.nuls.api.server.dao.mapper.DepositMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 委托
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class DepositBusiness {

    @Autowired
    private DepositMapper depositMapper;

    /**
     * 委托列表
     * @param address 账户地址
     * @return
     */
    public List<Deposit> getList(String address) {
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        return depositMapper.selectList(searchable);
    }

}
