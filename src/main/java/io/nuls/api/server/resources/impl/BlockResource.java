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
package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.Alias;
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
 * @author Niels
 * @date 2017/9/30
 */
@Path("/block")
@Component
public class BlockResource {


    @GET
    @Path("/hash/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult loadBlock(@PathParam("hash") String hash) {
        RpcClientResult result;
        if (!StringUtils.validHash(hash)) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RestFulUtils.getInstance().get("/block/hash/"+hash, null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/height/{height}")
    @Produces(MediaType.APPLICATION_JSON)

    public RpcClientResult getBlock(@PathParam("height") Long height) {
        RpcClientResult result;
        if (height < 0) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RestFulUtils.getInstance().get("/block/height/" + height, null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/newest")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult newest() {
        RpcClientResult result;
        try {
            result = RestFulUtils.getInstance().get("/block/newest", null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }


    @GET
    @Path("/header/height/{height}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getHeaderByHeight(@PathParam("height") Integer height) {
        RpcClientResult result;
        if (height < 0) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RestFulUtils.getInstance().get("/block/header/height/"+height, null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/header/hash/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getHeader(@PathParam("hash") String hash) {
        RpcClientResult result;
        if (!StringUtils.validHash(hash)) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RestFulUtils.getInstance().get("/block/header/hash/"+hash, null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/list/address")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getListByAddress(@QueryParam("address") String address, @QueryParam("type") int type
            , @QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize){
        if(!StringUtils.validAddress(address) || pageNumber < 0 || pageSize < 0 || type < 1 || type > 2 ){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 10;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        Map<String, String> param = new HashMap<>(4);
        param.put("address", address);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        param.put("type", String.valueOf(type));
        RpcClientResult result;
        try {
            result = RestFulUtils.getInstance().get("/block/list/address", param);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getList(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize){
        if(pageNumber < 0 || pageSize < 0){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 10;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        Map<String, String> param = new HashMap<>(2);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        RpcClientResult result;
        try {
            result = RestFulUtils.getInstance().get("/block/list", param);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }


    @POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult testMethod(Alias alias) {
        RpcClientResult result;
        try {
            result = RpcClientResult.getSuccess();
            result.setData(alias);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }


}
