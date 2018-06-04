package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.BalanceBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
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
    @Path("/get/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getBalance(@PathParam("address") String address){
        RpcClientResult result = null;
        result = RpcClientResult.getSuccess();
        result.setData(balanceBusiness.getBalance(address));
        return result;
    }
}
