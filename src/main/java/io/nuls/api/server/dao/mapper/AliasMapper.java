package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.Alias;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface AliasMapper extends BaseMapper<Alias, String>{

    void rollback(@Param("blockHeight") long blockHeight);
    int insertByBatch(@Param("list") List<Alias> list);
}