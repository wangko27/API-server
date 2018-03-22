package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Charlie
 * @date: 2018/3/19
 */
@Path("/consensus")
@Component
public class ConsensusResource {


    @GET
    @Path("/agent/address/{address}")
    public RpcClientResult queryAgent(@PathParam("address") String address){
        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        RpcClientResult result;
        try {
            result = RestFulUtils.getInstance().get("/consensus/agent/address/" + address, null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/agent/list")
    public RpcClientResult queryAgentList(@QueryParam("keyword") String keyword, @QueryParam("sortType") String sortType
            , @QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize){
        if(pageNumber < 0 || pageSize < 0){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 10;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        Map<String, String> param = new HashMap<>(4);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));

        if(StringUtils.isNotBlank(keyword)) {
            param.put("keyword", keyword);
        }
        if (StringUtils.isNotBlank(sortType)) {
            param.put("sortType", sortType);
        }
        RpcClientResult result;
        try {
            result = RestFulUtils.getInstance().get("/consensus/agent/list", param);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/agent/status")
    public RpcClientResult queryAllOfAgentStatus(){
        RpcClientResult result;
        try {
            result = RestFulUtils.getInstance().get("/consensus/agent/status", null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    public RpcClientResult queryAllAgentStatistics(){
        RpcClientResult result;
        try {
            result = RestFulUtils.getInstance().get("/consensus", null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/deposit/address/{address}")
    public RpcClientResult queryDepositByAddress(@PathParam("address") String address, @QueryParam("agentAddress") String agentAddress
            , @QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize){
        if(!StringUtils.validAddress(address) || pageNumber < 0 || pageSize < 0){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if(StringUtils.isNotBlank(agentAddress) && !StringUtils.validAddress(agentAddress)){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 10;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        RpcClientResult result;
        Map<String, String> param = new HashMap<>(4);
        param.put("address", address);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        if (StringUtils.isNotBlank(agentAddress)) {
            param.put("agentAddress", agentAddress);
        }
        try {
            result = RestFulUtils.getInstance().get("/consensus/deposit/address/"+ address, null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
