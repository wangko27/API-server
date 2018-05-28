package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.Alias;

public interface AliasMapper {
    int deleteByPrimaryKey(String alias);

    int insert(Alias record);

    int insertSelective(Alias record);

    Alias selectByPrimaryKey(String alias);

    int updateByPrimaryKeySelective(Alias record);

    int updateByPrimaryKey(Alias record);
}