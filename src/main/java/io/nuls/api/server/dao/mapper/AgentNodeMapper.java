package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.AgentNode;

public interface AgentNodeMapper {
    int deleteByPrimaryKey(String txHash);

    int insert(AgentNode record);

    int insertSelective(AgentNode record);

    AgentNode selectByPrimaryKey(String txHash);

    int updateByPrimaryKeySelective(AgentNode record);

    int updateByPrimaryKey(AgentNode record);
}