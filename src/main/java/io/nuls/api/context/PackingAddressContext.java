package io.nuls.api.context;

import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 出块账户静态统计数据（数据库）
 * Author: zsj
 * Date:  2018/6/5 0005
 */
public class PackingAddressContext {
    /**
     * 当前正在共识的节点数量
     */
    public static int consensusAgentCount = 0;
    /**
     * 正在共识的节点的总委托
     */
    public static Long consensusAgentDepositAmount = 0L;
    /*private static List<AgentNodeDto> agentNodeList = new ArrayList<>();*/
    private static Map<String, AgentNodeDto> agentNodeMap = new ConcurrentHashMap<>();
    public static void add(AgentNodeDto block){
        if(null != block && StringUtils.validAddress(block.getAgentAddress())){
            agentNodeMap.put(block.getAgentAddress(),block);
        }
    }

    public static Integer getSize(){
        return agentNodeMap.size();
    }
    public static List<AgentNodeDto> getAllList(){
        List<AgentNodeDto> agentNodeList = new ArrayList<>();
        for(AgentNodeDto agentNodeDto : agentNodeMap.values()){
            agentNodeList.add(agentNodeDto);
        }
        Collections.sort(agentNodeList, new Comparator<AgentNodeDto>() {
            @Override
            public int compare(AgentNodeDto o1, AgentNodeDto o2) {
                return o1.getTotalPackingCount()>o2.getTotalPackingCount()?-1:1;
            }
        });
        return agentNodeList;
    }
    public static Map<String, AgentNodeDto> getAll(){
        return agentNodeMap;
    }

    public static void clear(){
        //agentNodeMap.clear();
        agentNodeMap =  new ConcurrentHashMap<>();
    }
    public static void reset(Map<String, AgentNodeDto> map){
        clear();
        agentNodeMap = map;
    }
}
