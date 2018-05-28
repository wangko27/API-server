package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.PunishLog;

public interface PunishLogMapper {
    int deleteByPrimaryKey(String id);

    int insert(PunishLog record);

    int insertSelective(PunishLog record);

    PunishLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PunishLog record);

    int updateByPrimaryKeyWithBLOBs(PunishLog record);

    int updateByPrimaryKey(PunishLog record);
}