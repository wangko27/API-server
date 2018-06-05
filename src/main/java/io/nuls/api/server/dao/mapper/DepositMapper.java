package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.Deposit;
import org.apache.ibatis.annotations.Param;

@MyBatisMapper
public interface DepositMapper extends BaseMapper<Deposit, String> {

    void deleteByAgentHash(@Param("agentHash") String agentHash, @Param("deleteHash") String deleteHash);
}