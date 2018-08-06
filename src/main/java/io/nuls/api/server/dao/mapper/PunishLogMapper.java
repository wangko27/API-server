package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.PunishLog;
import io.nuls.api.server.dao.util.Searchable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface PunishLogMapper extends BaseMapper<PunishLog, Long >{
    int insertByBatch(@Param("list") List<PunishLog> list);

    /**
     * 统计数量
     * @param searchable
     * @return
     */
    int selectCountSearchable(Searchable searchable);
}