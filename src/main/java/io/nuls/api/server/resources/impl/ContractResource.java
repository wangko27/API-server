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

import io.nuls.api.constant.ContractErrorCode;
import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.ContractBusiness;
import io.nuls.api.utils.AddressTool;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Description: 智能合约相关API
 * Author: qinyf
 * Date:  2018/9/13
 */
@Path("/contracts")
@Component
public class ContractResource {

    @Autowired
    private ContractBusiness contractBusiness;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractList(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
        RpcClientResult result = RpcClientResult.getSuccess();
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
        result.setData(contractBusiness.getContractInfoList(pageNumber, pageSize));
        return result;
    }

    @GET
    @Path("/{contractAddress}/transaction/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractTransactionDetail(@PathParam("contractAddress") String contractAddress, @PathParam("hash") String hash){
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(contractBusiness.getContractTransactionDetail(hash,contractAddress));
        return result;
    }

    @GET
    @Path("/{contractAddress}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractList(@PathParam("contractAddress") String contractAddress) {
        RpcClientResult result = RpcClientResult.getSuccess();
        if (contractAddress == null) {
            return RpcClientResult.getFailed(ContractErrorCode.PARAMETER_ERROR);
        }
        if (!AddressTool.validAddress(contractAddress)) {
            return RpcClientResult.getFailed(ContractErrorCode.ILLEGAL_CONTRACT_ADDRESS);
        }
        result.setData(contractBusiness.getContractInfo(contractAddress));
        return result;
    }

    @GET
    @Path("/{contractAddress}/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getContractTxList(@PathParam("contractAddress") String contractAddress, @QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @QueryParam("accountAddress") String accountAddress) {
        RpcClientResult result = RpcClientResult.getSuccess();
        //参数校验
        if (contractAddress == null) {
            return RpcClientResult.getFailed(ContractErrorCode.PARAMETER_ERROR);
        }
        if (!AddressTool.validAddress(contractAddress)) {
            return RpcClientResult.getFailed(ContractErrorCode.ILLEGAL_CONTRACT_ADDRESS);
        }

        if (!StringUtils.isBlank(accountAddress) && !AddressTool.validAddress(accountAddress)) {
            return RpcClientResult.getFailed(ContractErrorCode.ADDRESS_ERROR);
        }

        result.setData(contractBusiness.getContractTxList(contractAddress, accountAddress, pageNumber, pageSize));
        return result;
    }


}
