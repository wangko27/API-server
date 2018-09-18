package io.nuls.api.server.resources.impl;

import io.nuls.api.entity.RpcClientResult;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Description: 首页
 * Author: moon
 * Date:  2018/8/28 0028
 */
@Path("/")
@Component
public class IndexResource {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult history(){
        RpcClientResult result = RpcClientResult.getSuccess();
        return result;
    }
}
