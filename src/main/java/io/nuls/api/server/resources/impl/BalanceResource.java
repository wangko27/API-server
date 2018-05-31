package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.BalanceBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Description: 账户资产
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Path("balance")
@Component
public class BalanceResource {

    @Autowired
    private BalanceBusiness balanceBusiness;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult list(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("address")String address){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            return result;
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        result = RpcClientResult.getSuccess();
        result.setData(balanceBusiness.getList(address));
        return result;
    }
}
