package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AddressRewardDetailBusiness;
import io.nuls.api.server.business.AgentNodeBusiness;
import io.nuls.api.server.business.BalanceBusiness;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * Description: 奖励
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Path("reward")
@Component
public class AddressRewardDetailResource {

    @Autowired
    private AddressRewardDetailBusiness addressRewardDetailBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;
    @Autowired
    private BalanceBusiness balanceBusiness;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getDepositList(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize, @QueryParam("address") String address){
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
        if(!StringUtils.validAddress(address)){
            result = RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        result.setData(addressRewardDetailBusiness.getList(address,pageNumber,pageSize));
        return result;
    }
    @GET
    @Path("/address/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getConsensusDetail(@PathParam("address") String address){
        RpcClientResult result = null;
        if(!StringUtils.validAddress(address)){
            result = RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        HashMap<String,String> attr = new HashMap<>();
        attr.put("reward",addressRewardDetailBusiness.selectSumReward(address).toString());
        AgentNode agentNode = agentNodeBusiness.getAgentByAddress(address);
        if(null != agentNode){
            attr.put("totalDeposit",agentNode.getTotalDeposit().toString());
        }else{
            attr.put("totalDeposit","0");
        }
        //todo 可用余额
        result.setData(attr);
        return result;
    }

}
