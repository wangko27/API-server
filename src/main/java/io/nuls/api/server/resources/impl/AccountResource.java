package io.nuls.api.server.resources.impl;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    public RpcClientResult account(@PathParam("address") String address){
        RpcClientResult result;
        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed();
        }
        try{
            result = RestFulUtils.getInstance().get("/account/" + address, null);
            if(!result.isSuccess()){
                return RpcClientResult.getFailed();
            }
            RpcClientResult resultBalance = RestFulUtils.getInstance().get("/account/balance/" + address, null);
            if(!resultBalance.isSuccess()){
                return RpcClientResult.getFailed();
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
    @Path("/balance/{address}")
    public RpcClientResult balance(@PathParam("address") String address){
        RpcClientResult result;
        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed();
        }
        try{
            result = RestFulUtils.getInstance().get("/account/balance/" + address, null);
            System.out.println(result.getData() instanceof Map);
        }catch (Exception e){
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }


}
