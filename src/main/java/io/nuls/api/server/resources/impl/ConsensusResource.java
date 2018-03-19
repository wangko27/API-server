package io.nuls.api.server.resources.impl;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author: Charlie
 * @date: 2018/3/19
 */
@Path("/consensus")
@Component
public class ConsensusResource {

    @GET
    @Path("/info/{address}")
    public RpcClientResult info(@PathParam("address") String address){
        RpcClientResult result;
        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed();
        }
        try {
            result = RestFulUtils.getInstance().get("/consensus/info/" + address, null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
