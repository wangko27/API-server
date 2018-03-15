package io.nuls.api.fiter;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.log.Log;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionFilter implements ExceptionMapper<Exception>{

    @Override
    public Response toResponse(Exception e) {
        Log.error(e);
        RpcClientResult result = RpcClientResult.getFailed(e.getMessage());
        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
}
