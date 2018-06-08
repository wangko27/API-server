package io.nuls.api.server.dto;

import io.nuls.api.entity.AgentNode;

/**
 * Description:
 * Author: zsj
 * Date:  2018/6/5 0005
 */
public class AgentNodeDto extends AgentNode {
    private Long totalPackingCount;
    private AgentNode agentNode;

    public AgentNodeDto(){

    }
    public AgentNodeDto(AgentNode entity){
        this.agentNode = entity;
    }

    @Override
    public Long getTotalPackingCount() {
        return totalPackingCount;
    }

    @Override
    public void setTotalPackingCount(Long totalPackingCount) {
        this.totalPackingCount = totalPackingCount;
    }

    public AgentNode getAgentNode() {
        return agentNode;
    }

    public void setAgentNode(AgentNode agentNode) {
        this.agentNode = agentNode;
    }
}
