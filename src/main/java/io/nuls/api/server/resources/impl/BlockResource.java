package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.BlockBusiness;
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
@Path("block")
@Component
public class BlockResource {

    @Autowired
    private BlockBusiness blockBusiness;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult list(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            return result;
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        result = RpcClientResult.getSuccess();
        result.setData(blockBusiness.getList(null,pageNumber,pageSize));
        return result;
    }

    @GET
    @Path("/list/address")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult listByAddress(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @QueryParam("address") String address){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            return result;
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        if(StringUtils.validAddress(address)){
            result = RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        result.setData(blockBusiness.getList(address,pageNumber,pageSize));
        return result;
    }

    @GET
    @Path("/height/{height}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getBlockByHeight(@PathParam("height") Long height){
        RpcClientResult result;
        if (height < 0) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RpcClientResult.getSuccess();
            result.setData(blockBusiness.getBlockByHeight(height));
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/hash/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getBlockByHash(@PathParam("hash") String hash){
        RpcClientResult result;
        if (!StringUtils.validHash(hash)) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RpcClientResult.getSuccess();
            result.setData(blockBusiness.getBlockByHash(hash));
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
            result = RpcClientResult.getSuccess();
            result.setData(blockBusiness.getNewest());
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
