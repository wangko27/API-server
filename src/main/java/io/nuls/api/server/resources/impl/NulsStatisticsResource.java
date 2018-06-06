package io.nuls.api.server.resources.impl;

import io.nuls.api.context.BalanceListContext;
import io.nuls.api.context.HistoryContext;
import io.nuls.api.context.PackingAddressContext;
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
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(HistoryContext.getAll());
        return result;
    }

    @GET
    @Path("/balancelist")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult balancelist(){
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(BalanceListContext.getAll());
        return result;
    }

    @GET
    @Path("/minedlist")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult minedlist(){
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(PackingAddressContext.getAll());
        return result;
    }
}
