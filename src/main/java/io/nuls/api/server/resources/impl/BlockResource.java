package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.context.IndexContext;
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
 * Description: 区块相关
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Path("block")
@Component
public class BlockResource {

    @Autowired
    private BlockBusiness blockBusiness;

    /**
     * 获取最新几条区块信息 （区块链浏览器首页显示，因为首页访问频繁，所以这里单独做一个获取首页区块的接口，从缓存获取）
     * @return
     */
    @GET
    @Path("/index")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult index(){
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(IndexContext.getBlockList());
        return result;
    }

    /**
     * 获取区块列表，可根据地址过滤
     * @param pageNumber 页数
     * @param pageSize 每页大小
     * @param address 地址
     * @return
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult listByAddress(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @QueryParam("address") String address){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
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

    /**
     * 根据高度获取区块详情
     * @param height 高度
     * @return
     */
    @GET
    @Path("/height/{height}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getBlockByHeight(@PathParam("height") Long height){
        RpcClientResult result;
        if (height < 0) {
            return RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RpcClientResult.getSuccess();
            //加载最新块，计算确认次数
            BlockHeader localBest = blockBusiness.getNewest();
            if(null == localBest){
                return RpcClientResult.getFailed(KernelErrorCode.FAILED);
            }
            BlockHeader requestBlock = blockBusiness.getBlockByHeight(height);
            if(null == requestBlock){
                return RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
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

    /**
     * 根据hash获取区块详情
     * @param hash 区块hash
     * @return
     */
    @GET
    @Path("/hash/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getBlockByHash(@PathParam("hash") String hash){
        RpcClientResult result = null;
        if (!StringUtils.validHash(hash)) {
            result = RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
            return result;
        }
        try {
            result = RpcClientResult.getSuccess();

            //加载最新块，计算确认次数
            BlockHeader localBest = blockBusiness.getNewest();
            BlockHeader requestBlock = blockBusiness.getBlockByHash(hash);

            if(null == localBest){
                return RpcClientResult.getFailed(KernelErrorCode.FAILED);
            }
            if(null == requestBlock){
                return RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
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

    /**
     * 获取最新区块
     * @return
     */
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
