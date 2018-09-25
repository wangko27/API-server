package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.business.UtxoBusiness;
import io.nuls.api.server.dto.TransFeeDto;
import io.nuls.api.utils.AddressTool;
import io.nuls.api.utils.TransactionFeeCalculator;
import io.nuls.api.utils.TransactionTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: utxo相关
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Path("utxo")
@Component
public class UtxoResource {

    @Autowired
    private UtxoBusiness utxoBusiness;

    /**
     * 获取某地址的冻结列表
     * @param pageNumber 页数
     * @param pageSize 每页大小
     * @param address 地址
     * @return
     */
    @GET
    @Path("/list/address")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult listWithAddress(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("address")String address){
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
        result.setData(utxoBusiness.getListByAddress(address,pageNumber,pageSize));
        return result;
    }

    /**
     * 获取某地址的可用的UTXO数量和总额—做零钱换整功能
     * @param address 地址
     * @return
     */
    @GET
    @Path("/getTotalUTXO/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getTotalUTXO(@PathParam("address")String address){
        RpcClientResult result = RpcClientResult.getSuccess();
        List<Utxo> list = utxoBusiness.getUsableUtxo(address);
        Long sum = list.stream().mapToLong(e -> e.getAmount()).sum();
        Map<String, String> map = new HashMap<>();
        map.put("size", list.size() + "");
        map.put("sum", sum + "");
        result.setData(map);
        return result;
    }

    /**
     * 预估手续费—做零钱换整功能
     * @param address 地址
     * @return
     */
    @GET
    @Path("/estimateFee/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult estimateFee(@PathParam("address")String address){
        RpcClientResult result = RpcClientResult.getSuccess();
        if (address == null) {
            return RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
        }
        if (!AddressTool.validAddress(address)) {
            return RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
        }
        List<Utxo> list = utxoBusiness.getUsableUtxo(address);
        Long sum = list.stream().mapToLong(e -> e.getAmount()).sum();
        TransFeeDto transferTxFee = TransactionTool.getChangeTxFee(list, sum, "", TransactionFeeCalculator.MIN_PRECE_PRE_1024_BYTES.getValue());
        Long fee = transferTxFee.getNa().getValue();
        Map<String, Long> map = new HashMap<>();
        map.put("fee", fee);
        result.setData(transferTxFee);
        return result;
    }

}
