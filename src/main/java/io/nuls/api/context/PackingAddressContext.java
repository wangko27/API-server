package io.nuls.api.context;

import io.nuls.api.server.dto.AgentNodeDto;

import java.util.ArrayList;
import java.util.List;

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
    private static List<AgentNodeDto> agentNodeList = new ArrayList<>();
    public static List<AgentNodeDto> getAllList(){
        return agentNodeList;
    }
    public static void clear(){
        //agentNodeMap.clear();
        agentNodeList.clear();
    }
    public static void reset(List<AgentNodeDto> list){
        agentNodeList = list;
    }
    public static int getSize(){
        return agentNodeList.size();
    }

}
