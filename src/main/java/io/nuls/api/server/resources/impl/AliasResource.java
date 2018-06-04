package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AliasBusiness;
import io.nuls.api.server.business.TransactionBusiness;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Description:
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Path("alias")
@Component
public class AliasResource {

    @Autowired
    private AliasBusiness aliasBusiness;

    @GET
    @Path("/get/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getAlias(@PathParam("address") String address){
        RpcClientResult result = null;
        if (!StringUtils.validAddress(address)) {
            result = RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
            return result;
        }
        try {
            result = RpcClientResult.getSuccess();
            result.setData(aliasBusiness.getAliasByAddress(address));
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
