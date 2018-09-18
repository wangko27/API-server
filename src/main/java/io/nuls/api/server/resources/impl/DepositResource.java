package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.DepositBusiness;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Description: 委托
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Path("deposit")
@Component
public class DepositResource {

    @Autowired
    private DepositBusiness depositBusiness;

    /**
     * 获取某地址委托列表，如果传入了agenthash，则查询该节点下的某地址的委托记录
     * @param pageNumber 每页大小
     * @param pageSize 页数
     * @param address 地址
     * @param agentHash 节点hash
     * @return
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getDepositList(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @QueryParam("address") String address, @QueryParam("agentHash") String agentHash){
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
        if(!StringUtils.validAddress(address)){
            result = RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        result.setData(depositBusiness.getList(address,agentHash,pageNumber,pageSize));
        return result;
    }
}
