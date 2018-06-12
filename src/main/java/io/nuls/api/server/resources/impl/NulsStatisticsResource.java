package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.context.BalanceListContext;
import io.nuls.api.context.HistoryContext;
import io.nuls.api.context.PackingAddressContext;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.Page;
import io.nuls.api.server.dto.UtxoDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    public RpcClientResult balancelist(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize){
        if (pageNumber < 0 || pageSize < 0) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        RpcClientResult result = RpcClientResult.getSuccess();
        Page<UtxoDto> page = new Page<>();
        page.setList(BalanceListContext.getAll());
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        page.setTotal(BalanceListContext.getSize());
        page.setPageList();
        result.setData(page);
        return result;
    }

    @GET
    @Path("/minedlist")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult minedlist(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize){
        if (pageNumber < 0 || pageSize < 0) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        RpcClientResult result = RpcClientResult.getSuccess();
        Page<AgentNodeDto> page = new Page<>();
        page.setList(PackingAddressContext.getAllList());
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);
        page.setTotal(PackingAddressContext.getSize());
        page.setPageList();
        result.setData(page);
        return result;
    }
}
