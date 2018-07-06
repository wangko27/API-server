package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.Deposit;
import io.nuls.api.server.dao.util.Searchable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface DepositMapper extends BaseMapper<Deposit, String> {

    void deleteByAgentHash(@Param("agentHash") String agentHash, @Param("deleteHash") String deleteHash);

    void rollbackStopAgent(@Param("deleteHash") String deleteHash);

    Integer selectTotalCount(Searchable searchable);

    Long selectTotalAmount(Searchable searchable);

    int insertByBatch(@Param("list") List<Deposit> list);
}