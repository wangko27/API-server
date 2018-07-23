package io.nuls.api.server.task;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.IndexContext;
import io.nuls.api.context.PackingAddressContext;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AgentNodeBusiness;
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
     * 去查询最新的节点列表
     */
    public void execute() {
        IndexContext.resetRpcAgentNodeList(sync(new ArrayList<>(),1,100));
        //加载全网节点到list中
        /*int consensusAgentCount = 0;
        for(Object object: list) {
            LinkedHashMap map = (LinkedHashMap) object;
            nodeList.add(map);
            if(map.containsKey("status")){
                Integer status = Integer.parseInt(map.get("status")+"");
                if(status == EntityConstant.CONSENSUS_STATUS_CONSENSUSING){
                    consensusAgentCount++;
                }
            }
        }
        PackingAddressContext.consensusAgentCount = consensusAgentCount;
        */
        //重置共识信息
        RpcClientResult rpcClientResult = agentNodeBusiness.getConsensus();
        if(rpcClientResult.isSuccess()){
            IndexContext.resetRpcConsensusData((Map)rpcClientResult.getData());
        }
    }

    /**
     * 加载所有链上的共识节点
     * @param list
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public List<LinkedHashMap> sync(List<LinkedHashMap> list,int pageNumber,int pageSize){
        RpcClientResult rpcClientResult = agentNodeBusiness.getList(null,pageNumber,pageSize);
        if(rpcClientResult.isSuccess()){
            Map<String,Object> map = (Map<String,Object>)rpcClientResult.getData();
            List<LinkedHashMap> rpcList = (List<LinkedHashMap>)map.get("list");
            list.addAll(rpcList);
            Integer total = Integer.valueOf(map.get("total")+"");
            if(list.size() < total){
                sync(list,pageNumber+1,pageSize);
            }
        }
        return list;
    }
}
