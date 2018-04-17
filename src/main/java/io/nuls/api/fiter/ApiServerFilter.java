/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.api.fiter;


import io.nuls.api.constant.Constant;
import io.nuls.api.counter.RequestCounter;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.PropertiesUtils;
import io.nuls.api.utils.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.concurrent.ExecutionException;


public class ApiServerFilter implements ContainerRequestFilter, ContainerResponseFilter, ExceptionMapper<Exception> {

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext.setProperty("start", System.currentTimeMillis());
        String remoteIP = request.getRemoteAddr();
        int count = 0;
        try {
            count = RequestCounter.increment(remoteIP);
        } catch (ExecutionException e) {
            Log.warn("RequestCounter increment unsuccessful, remoteIP: " + remoteIP);
        }
        if(count > Integer.valueOf(PropertiesUtils.readProperty(Constant.REQUEST_FREQUENCY, Constant.DEFAULT_REQUEST_FREQUENCY))) {
            Log.warn("A service request has been rejected due to more frequently request("+count+"/s), remoteIP: " + remoteIP);
            RpcClientResult result = RpcClientResult.getFailed("Server Rejected.");
            requestContext.abortWith(Response.ok(result, MediaType.APPLICATION_JSON).build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Log.info(
                //"url:{},Remote_IP:{},useTime:{}, params:{},result:{}",
                //requestContext.getUriInfo().getRequestUri().getPath() + "?" + requestContext.getUriInfo().getRequestUri().getQuery(), request.getRemoteAddr(), (System.currentTimeMillis() - Long.parseLong(requestContext.getProperty("start").toString())), null, responseContext.getEntity());
                "url:{},Remote_IP:{},useTime:{}, params:{}",
                requestContext.getUriInfo().getRequestUri().getPath() + "?" + requestContext.getUriInfo().getRequestUri().getQuery(), request.getRemoteAddr(), (System.currentTimeMillis() - Long.parseLong(requestContext.getProperty("start").toString())), null);
    }

    @Override
    public Response toResponse(Exception e) {
        Log.error(e);
        RpcClientResult result = RpcClientResult.getFailed(e.getMessage());
        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }

}
