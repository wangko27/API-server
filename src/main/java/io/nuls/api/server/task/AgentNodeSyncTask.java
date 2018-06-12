package io.nuls.api.server.task;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.PackingAddressContext;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AgentNodeBusiness;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 每隔10s去链上查询出块节点信息
 */
@Component
public class AgentNodeSyncTask {

    @Autowired
    private AgentNodeBusiness agentNodeBusiness;

    /**
     * 去链上查询最新的节点列表，根据节点状态修改内存中的节点的状态，并统计出块节点数量和总的共识中的委托金额数量
     */
    public void execute() {
        Map<String, AgentNodeDto> agentNodeMap = PackingAddressContext.getAll();
        List<Object> list = sync(new ArrayList<>(),1,100);
        int consensusAgentCount = 0;
        Long consensusAgentDepositAmount = 0L;
        for(Object object: list){
            LinkedHashMap map = (LinkedHashMap)object;
            if(map.containsKey("agentAddress") && map.containsKey("status")){
                String agentAddress = map.get("agentAddress").toString();
                if(StringUtils.validAddress(agentAddress)){
                    AgentNodeDto dto = agentNodeMap.get(agentAddress);
                    if(null != dto){
                        Integer status = Integer.parseInt(map.get("status")+"");
                        if(status == EntityConstant.CONSENSUS_STATUS_CONSENSUSING){
                            consensusAgentCount++;
                            if(null != dto.getTotalDeposit()){
                                consensusAgentDepositAmount += dto.getTotalDeposit();
                            }
                        }
                        dto.setStatus(status);
                        agentNodeMap.get(agentAddress).setStatus(status);
                    }
                }
            }
        }
        PackingAddressContext.reset(agentNodeMap);
        PackingAddressContext.consensusAgentCount = consensusAgentCount;
        PackingAddressContext.consensusAgentDepositAmount = consensusAgentDepositAmount;
    }

    public List<Object> sync(List<Object> list,int pageNumber,int pageSize){
        RpcClientResult rpcClientResult = agentNodeBusiness.getList(null,pageNumber,pageSize);
        if(rpcClientResult.isSuccess()){
            Map<String,Object> map = (Map<String,Object>)rpcClientResult.getData();
            List<Object> rpcList = (List<Object>)map.get("list");
            list.addAll(rpcList);
            Integer total = Integer.valueOf(map.get("total")+"");
            if(list.size() < total){
                sync(list,pageNumber+1,pageSize);
            }
        }
        return list;
    }
}
