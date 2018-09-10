package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.BalanceBusiness;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Description: 账户资产
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Path("/balance")
@Component
public class BalanceResource {

    @Autowired
    private BalanceBusiness balanceBusiness;

    @GET
    @Path("/get/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getBalance(@PathParam("address") String address){
        RpcClientResult result = null;
        if (!StringUtils.validAddress(address)) {
            result = RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        result.setData(balanceBusiness.getBalance(address));
        return result;
    }
}
