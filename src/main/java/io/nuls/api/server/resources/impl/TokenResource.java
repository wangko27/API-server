/*
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
 *
 */
package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.entity.ContractTokenInfo;
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
import java.util.Map;
import java.util.HashMap;

/**
 * Description:代币接口
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Path("tokens")
@Component
public class TokenResource {

    @Autowired
    private ContractBusiness contractBusiness;

    /**
     * 根据合约地址获取代币转账列表
     * @param pageNumber
     * @param pageSize
     * @param contractAddress       合约地址
     * @return
     */
    @GET
    @Path("/{contractAddress}/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractTokenTransferInfosByContractAddress(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @PathParam("contractAddress") String contractAddress){
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
        result.setData(contractBusiness.getContractTokenTransferInfosByContractAddress(contractAddress,pageNumber,pageSize));
        return result;
    }

    /**
     * 根据钱包地址获取代币转账列表
     * @param pageNumber
     * @param pageSize
     * @param accountAddress       NULS钱包地址
     * @return
     */
    @GET
    @Path("/transactions/{accountAddress}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractTokenTransferInfosByAccountAddress(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @PathParam("accountAddress") String accountAddress){
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
        result.setData(contractBusiness.getContractTokenTransferInfosByAccountAddress(accountAddress,pageNumber,pageSize));
        return result;
    }

    /**
     * 根据合约地址获取指定代币持有者列表
     * @param pageNumber
     * @param pageSize
     * @param contractAddress       合约地址
     * @return
     */
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
    public RpcClientResult getContractTokenAssetsDetails(@PathParam("contractAddress") String contractAddress, @PathParam("address") String address){
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(contractBusiness.getContractTokenAssetsDetails(address,contractAddress));
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

    /**
     * Description:get all tokens list
     * Author: Flyglded
     * Date:  2018/9/14 0029
     */
    @GET
    @Path("/wallet/{accountAddress}")
    @Produces(MediaType.APPLICATION_JSON)
    public  RpcClientResult getTokensByAccountAddress(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @PathParam("accountAddress") String accountAddress) {
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
        result.setData(contractBusiness.getContractTokeninfoListByAccountAddress(pageNumber,pageSize,accountAddress));
        return result;
    }

    /**
     * Description:get all tokens list
     * Author: Flyglded
     * Date:  2018/9/14 0029
     */
    @GET
    @Path("/{contractAddress}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getTokenInfo(@PathParam("contractAddress") String contractAddress) {
        RpcClientResult result = null;
        ContractTokenInfo contractTokenInfo = null;
        ContractAddressInfo contractAddressInfo = null;
        Map<String, Object> mapToken = new HashMap<String, Object>();
        if(StringUtils.isBlank(contractAddress)) {
            result = RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
            return result;
        }
        contractTokenInfo = contractBusiness.getContractTokenInfo(contractAddress);
        if(null == contractTokenInfo) {
            result = RpcClientResult.getFailed(KernelErrorCode.DATA_ERROR);
            return result;
        }
        contractAddressInfo = contractBusiness.getContractAddressInfo(contractAddress);
        long totalTransfers = contractBusiness.selectTotalTransfer(contractAddress);
        long totalHolders = contractBusiness.selectTotalHolders(contractAddress);
        mapToken.put("contractAddress",contractTokenInfo.getContractAddress());
        mapToken.put("tokenName",contractTokenInfo.getTokenName());
        mapToken.put("symbol",contractTokenInfo.getSymbol());
        mapToken.put("decimals",contractTokenInfo.getDecimals());
        mapToken.put("totalSupply",contractTokenInfo.getTotalsupply());
        mapToken.put("blockHeight",contractAddressInfo.getBlockHeight());
        mapToken.put("createrAddress",contractAddressInfo.getCreater());
        mapToken.put("txHash",contractTokenInfo.getTxHash());
        mapToken.put("createTime",contractTokenInfo.getCreateTime());
        mapToken.put("isNrc20",contractAddressInfo.getIsNrc20());
        mapToken.put("status",contractAddressInfo.getStatus());
        mapToken.put("transfers",totalTransfers);
        mapToken.put("holders",totalHolders);
        result = RpcClientResult.getSuccess();
        result.setData(mapToken);
        return result;
    }
}