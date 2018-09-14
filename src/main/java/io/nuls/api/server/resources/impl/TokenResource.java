package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.ContractBusiness;
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
@Path("tokens")
@Component
public class TokenResource {

    @Autowired
    private ContractBusiness contractBusiness;

    @GET
    @Path("/{contractAddress}/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractTokenTransfers(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @PathParam("contractAddress") String contractAddress){
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
        result.setData(contractBusiness.getContractTokenTransfers(contractAddress,pageNumber,pageSize));
        return result;
    }

    @GET
    @Path("/{contractAddress}/holders")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractTokenAssets(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @PathParam("contractAddress") String contractAddress){
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
        result.setData(contractBusiness.getContractTokenAssets(contractAddress,pageNumber,pageSize));
        return result;
    }

    @GET
    @Path("/{contractAddress}/holder/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractTokenAssetsDetails(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @PathParam("contractAddress") String contractAddress, @PathParam("address") String address){
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
        result.setData(contractBusiness.getContractTokenAssetsDetails(address,contractAddress,pageNumber,pageSize));
        return result;
    }

    /**
     * Description:get all tokens list
     * Author: Flyglded
     * Date:  2018/9/14 0029
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public  RpcClientResult getTokens(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
        RpcClientResult result = null;
        if(pageNumber <0 || pageSize < 0) {
            result = RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
            return result;
        }
        if(pageNumber == 0) {
            pageNumber = 1;
        }
        if(pageSize == 0) {
            pageSize = 20;
        }
        if(pageSize > 100) {
            pageSize = 100;
        }
        result = RpcClientResult.getSuccess();
        result.setData(contractBusiness.getContractTokeninfoList(pageNumber,pageSize));
        return result;
    }

    @GET
    @Path("/{contractAddress}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getTokenInfo(@QueryParam("contractAddress") String contractAddress) {
        RpcClientResult result = null;
        if(StringUtils.isBlank(contractAddress)) {
            result = RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        return result;
    }
}