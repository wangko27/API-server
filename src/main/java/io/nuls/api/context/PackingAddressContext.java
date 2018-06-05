package io.nuls.api.context;

import io.nuls.api.entity.AgentNode;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.UtxoDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 出块账户静态统计
 * Author: zsj
 * Date:  2018/6/5 0005
 */
public class PackingAddressContext {
    private static List<AgentNodeDto> agentNodeList = new ArrayList<>();
    public static void add(AgentNodeDto block){
        agentNodeList.add(block);
    }

    public static List<AgentNodeDto> getAll(){
        return agentNodeList;
    }

    public static void clear(){
        agentNodeList.clear();
    }
    public static void reset(List<AgentNodeDto> list){
        clear();
        agentNodeList = list;
        for(AgentNodeDto dto:agentNodeList){
            System.out.println(dto.getTotalPackingCount());
            System.out.println(dto.getTxHash());
            System.out.println("-----------");
        }
    }
}
