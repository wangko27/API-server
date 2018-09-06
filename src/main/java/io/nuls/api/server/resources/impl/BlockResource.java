package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.dto.BlockHeaderDto;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Description:
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Path("block")
@Component
public class BlockResource {

    @Autowired
    private BlockBusiness blockBusiness;

    @GET
    @Path("/index")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult index(){
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(IndexContext.getBlockList());
        return result;
    }

    @GET
    @Path("/list")
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
            //加载最新块，计算确认次数
            BlockHeader localBest = blockBusiness.getNewest();
            if(null == localBest){
                return RpcClientResult.getFailed(ErrorCode.FAILED);
            }
            BlockHeader requestBlock = blockBusiness.getBlockByHeight(height);
            if(null == requestBlock){
                return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            }
            BlockHeaderDto blockHeaderDto = new BlockHeaderDto(requestBlock);
            blockHeaderDto.setConfirmCount(localBest.getHeight()-requestBlock.getHeight());
            result.setData(blockHeaderDto);
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
        RpcClientResult result = null;
        if (!StringUtils.validHash(hash)) {
            result = RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            return result;
        }
        try {
            result = RpcClientResult.getSuccess();

            //加载最新块，计算确认次数
            BlockHeader localBest = blockBusiness.getNewest();
            BlockHeader requestBlock = blockBusiness.getBlockByHash(hash);

            if(null == localBest){
                return RpcClientResult.getFailed(ErrorCode.FAILED);
            }
            if(null == requestBlock){
                return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            }
            BlockHeaderDto blockHeaderDto = new BlockHeaderDto(requestBlock);
            blockHeaderDto.setConfirmCount(localBest.getHeight()-requestBlock.getHeight());
            result.setData(blockHeaderDto);
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
            BlockHeader blockHeader = blockBusiness.getNewest();
            //去掉不需要的返回值
            if(null != blockHeader){
                blockHeader.setExtend(null);
                blockHeader.setTxHashList(null);
            }
            result.setData(blockHeader);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
