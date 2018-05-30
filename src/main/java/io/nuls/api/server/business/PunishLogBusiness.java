package io.nuls.api.server.business;

import io.nuls.api.entity.PunishLog;
import io.nuls.api.server.dao.mapper.PunishLogMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 处罚日志
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class PunishLogBusiness {

    @Autowired
    private PunishLogMapper punishLogMapper;

    /**
     * 处罚列表
     * @param address 账户地址
     * @return
     */
    public List<PunishLog> getList(String address) {
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        return punishLogMapper.selectList(searchable);
    }

}
