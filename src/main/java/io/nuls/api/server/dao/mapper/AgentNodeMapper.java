package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.AgentNode;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.AgentNodeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface AgentNodeMapper extends BaseMapper<AgentNode, String>{

    void rollbackStopAgent(@Param("deleteHash") String deleteHash);

    /**
     * 统计出块排名
     * @param searchable
     * @return
     */
    List<AgentNodeDto> selectTotalpackingCount(Searchable searchable);

    /**
     * 根据Searchable 查询数据条数
     * @param searchable
     * @return
     */
    Integer selectTotalCount(Searchable searchable);

    int insertByBatch(@Param("list") List<AgentNode> list);
}