package io.nuls.api.server.resources.impl;

import io.nuls.api.entity.RpcClientResult;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 统计信息 （14天交易历史）
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Path("statistics")
@Component
public class NulsStatisticsResource {
    @GET
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult history(){
        RpcClientResult result = null;
        List<HashMap<String,String>> data = new ArrayList<>();
        HashMap<String,String> attr = new HashMap<String,String>();
        attr.put("id","50");
        attr.put("date","20180114");
        attr.put("txCount","12548");
        data.add(attr);
        result = RpcClientResult.getSuccess();
        result.setData(data);
        return result;
    }

    @GET
    @Path("/balancelist")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult balancelist(){
        //todo
        RpcClientResult result = null;
        List<HashMap<String,String>> data = new ArrayList<>();
        HashMap<String,String> attr = new HashMap<String,String>();
        attr.put("id","50");
        attr.put("date","20180114");
        attr.put("txCount","12548");
        data.add(attr);
        result = RpcClientResult.getSuccess();
        result.setData(data);
        return result;
    }

    @GET
    @Path("/minedlist")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult minedlist(){
        //todo
        RpcClientResult result = null;
        List<HashMap<String,String>> data = new ArrayList<>();
        HashMap<String,String> attr = new HashMap<String,String>();
        attr.put("id","50");
        attr.put("date","20180114");
        attr.put("txCount","12548");
        data.add(attr);
        result = RpcClientResult.getSuccess();
        result.setData(data);
        return result;
    }
}
