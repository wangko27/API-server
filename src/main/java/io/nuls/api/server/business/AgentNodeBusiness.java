package io.nuls.api.server.business;

import io.nuls.api.entity.AgentNode;
import io.nuls.api.server.dao.mapper.AgentNodeMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 节点
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class AgentNodeBusiness{

    @Autowired
    private AgentNodeMapper agentNodeMapper;

    /**
     * 获取列表
     * @param agentAddress 创建地址
     * @return
     */
    public List<AgentNode> getList(String agentAddress) {
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(agentAddress)){
            searchable.addCondition("agent_address", SearchOperator.eq, agentAddress);
        }
        return agentNodeMapper.selectList(searchable);
    }

    /**
     * 获取节点详情
     * @param agentAddress 创建地址
     * @return
     */
    public AgentNode getAgentDetail(String agentAddress) {
        Searchable searchable = new Searchable();
        searchable.addCondition("agent_address", SearchOperator.eq, agentAddress);
        return agentNodeMapper.selectBySearchable(searchable);
    }

}
