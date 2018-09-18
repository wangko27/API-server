package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.UtxoBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
}
