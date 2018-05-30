package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AgentNodeBusiness;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * Description:
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Path("consensus")
@Component
public class AgentNodeResource {

    @Autowired
    private AgentNodeBusiness agentNodeBusiness;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getConsensusStatistics(){
        RpcClientResult result = null;
        HashMap<String,String> attr = new HashMap<String,String>();
        attr.put("allConsensusNode","50");
        attr.put("consensusNode","42");
        attr.put("totalDeposit","45645.544");
        attr.put("consensusReward","4545");
        result = RpcClientResult.getSuccess();
        result.setData(attr);
        return result;
    }

    @GET
    @Path("/agent/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getConsensusList(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("agentAddress") String agentAddress){
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
        if(StringUtils.validAddress(agentAddress)){
            result = RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        result.setData(agentNodeBusiness.getList(agentAddress));
        return result;
    }

    @GET
    @Path("/agent/{agentAddress}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getConsensusDetail(@PathParam("agentAddress") String agentAddress){
        RpcClientResult result = null;
        if(StringUtils.validAddress(agentAddress)){
            result = RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        result.setData(agentNodeBusiness.getAgentDetail(agentAddress));
        return result;
    }
}
