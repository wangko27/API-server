package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.AgentNode;
import org.apache.ibatis.annotations.Param;

@MyBatisMapper
public interface AgentNodeMapper extends BaseMapper<AgentNode, String>{

    void rollbackStopAgent(@Param("deleteHash") String deleteHash);
}