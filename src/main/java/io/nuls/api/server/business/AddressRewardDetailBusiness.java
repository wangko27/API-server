package io.nuls.api.server.business;

import io.nuls.api.entity.AddressRewardDetail;
import io.nuls.api.server.dao.mapper.AddressRewardDetailMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 账户奖励
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class AddressRewardDetailBusiness {

    @Autowired
    private AddressRewardDetailMapper addressRewardDetailMapper;

    /**
     * 获取列表
     * @param address 奖励的账户地址
     * @return
     */
    public List<AddressRewardDetail> getList(String address) {
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        return addressRewardDetailMapper.selectList(searchable);
    }

}
