package io.nuls.api.server.business;

import io.nuls.api.entity.Alias;
import io.nuls.api.entity.Transaction;
import io.nuls.api.server.dao.mapper.AliasMapper;
import io.nuls.api.server.dao.mapper.TransactionMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 别名
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class AliasBusiness {

    @Autowired
    private AliasMapper aliasMapper;

    /**
     * 获取别名
     * @param address 账户地址
     * @return
     */
    public Alias getAliasDetail(String address) {
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        return aliasMapper.selectBySearchable(searchable);
    }

}
