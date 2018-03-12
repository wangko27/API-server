package io.nuls.api.server.resources.impl;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Component
public class BaseResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult test() {
        RpcClientResult result;
        try {
            //RestFulUtils.getInstance().init("http://127.0.0.1:8765/nuls");
            //result = RestFulUtils.getInstance().get("/report/mined", null);
            result = RpcClientResult.getSuccess();
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
