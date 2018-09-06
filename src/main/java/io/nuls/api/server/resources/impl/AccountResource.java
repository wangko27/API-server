package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.crypto.Hex;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.AddressTool;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Description:
 * Author: moon
 * Date:  2018/7/17 0017
 */
@Path("account")
@Component
public class AccountResource {

    @GET
    @Path("/getAddress/{publicKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getAlias(@PathParam("publicKey") String publicKey){
        //todo 之后用js获取
        RpcClientResult result = null;
        try {
            if(StringUtils.isBlank(publicKey)){
                result = RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
                return result;
            }
            result = RpcClientResult.getSuccess();
            result.setData(AddressTool.getStringAddress(Hex.decode(publicKey)));
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
