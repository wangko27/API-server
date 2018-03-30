package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.Na;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Charlie
 * @date: 2018/3/15
 */
@Path("/account")
@Component
public class AccountResource {

    @GET
    @Path("/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult account(@PathParam("address") String address){
        RpcClientResult result;
        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try{
            result = RestFulUtils.getInstance().get("/account/" + address, null);
            if(!result.isSuccess()){
                return result;
            }
            RpcClientResult resultBalance = RestFulUtils.getInstance().get("/account/balance/" + address, null);
            if(!resultBalance.isSuccess()){
                return result;
            }
            Map map = (Map)result.getData();
            map.putAll((Map)resultBalance.getData());
            result.setData(map);
        }catch (Exception e){
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }


    @GET
    @Path("/utxo")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult utxo(@QueryParam("address") String address, @QueryParam("amount") long amount){
        if(!StringUtils.validAddress(address) || amount <= 0 || amount > Na.MAX_NA_VALUE){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        RpcClientResult result;
        Map<String, String> param = new HashMap<>(2);
        param.put("address", address);
        param.put("amount", String.valueOf(amount));
        try {
            result = RestFulUtils.getInstance().get("/account/utxo", param);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

}
