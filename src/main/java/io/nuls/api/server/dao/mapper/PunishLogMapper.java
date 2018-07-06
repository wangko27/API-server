package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.PunishLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface PunishLogMapper extends BaseMapper<PunishLog, Long >{
    int insertByBatch(@Param("list") List<PunishLog> list);
}