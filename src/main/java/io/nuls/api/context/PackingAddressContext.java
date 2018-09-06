package io.nuls.api.context;

import io.nuls.api.server.dto.AgentNodeDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 出块账户静态统计数据（数据库）
 * Author: moon
 * Date:  2018/6/5 0005
 */
public class PackingAddressContext {

    private static List<AgentNodeDto> agentNodeList = new ArrayList<>();
    public static List<AgentNodeDto> getAllList(){
        return agentNodeList;
    }
    public static void reset(List<AgentNodeDto> list){
        agentNodeList = list;
    }
    public static int getSize(){
        return agentNodeList.size();
    }

}
