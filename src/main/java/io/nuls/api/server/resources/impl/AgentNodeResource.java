package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.constant.ErrorCode;
import io.nuls.api.context.HistoryContext;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.Balance;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.*;
import io.nuls.api.server.dto.AgentDto;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    private AddressRewardDetailBusiness addressRewardDetailBusiness;
    @Autowired
    private BalanceBusiness balanceBusiness;
    @Autowired
    private DepositBusiness depositBusiness;
    @Autowired
    private PunishLogBusiness punishLogBusiness;

    /**
     * 统计全网共识信息
     * @return
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getConsensusStatistics(){
        RpcClientResult result = null;
        HashMap<String,String> attr = new HashMap<String,String>();
        Long totalDeposit = 0L;
        Integer totalCount = 0;
        Long consensusAccountNumber = 0L;
        try{
            Map rpcdata = IndexContext.getRpcConsensusData();
            totalDeposit = Long.valueOf(rpcdata.get("totalDeposit")+"");
            totalCount = Integer.valueOf(rpcdata.get("agentCount")+"");
            consensusAccountNumber = Long.valueOf(rpcdata.get("consensusAccountNumber")+"");
        }catch (NullPointerException e){
            Log.error("获取全网共识信息失败");
        }
        attr.put("agentCount",totalCount+"");
        attr.put("rewardOfDay", HistoryContext.rewardofday+"");
        attr.put("consensusAccountNumber",consensusAccountNumber+"");
        attr.put("totalDeposit",totalDeposit+"");
        result = RpcClientResult.getSuccess();
        result.setData(attr);
        return result;
    }

    /**
     * 根据页数查询节点列表，由于信用值没办法及时获取，只能每次调用都去链上查询
     * @param pageNumber
     * @param pageSize
     * @param agentName
     * @return
     */
    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getConsensusList(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("agentName") String agentName,@QueryParam("status") Integer status,@QueryParam("sort") int sort){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
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
        result.setData(IndexContext.getAgentNodeList(pageNumber,pageSize,agentName,status,sort));
        return result;
    }

    /**
     * 我委托了的共识节点列表
     * @param pageNumber
     * @param pageSize
     * @param address
     * @return
     */
    @GET
    @Path("/list/address")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getMyConsensusList(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("address") String address,@QueryParam("agentName")String agentName){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
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
        result.setData(agentNodeBusiness.getMy(address,agentName,pageNumber,pageSize));
        return result;
    }

    /**
     * 根据agentAddress获取节点详情(由于需要展示共识状态等信息，需要链上直接查询加上本地查询)
     * @param address
     * @return
     */
    @GET
    @Path("/agent/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getConsensusDetail(@PathParam("address") String address){
        RpcClientResult result = null;

        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
        }
        AgentNode agentNode = agentNodeBusiness.getAgentByAddress(address);
        if(null == agentNode){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        result = RpcClientResult.getSuccess();
        //链上加载共识状态
        RpcClientResult rpcClientResult = agentNodeBusiness.getAgentByAddressWithRpc(agentNode.getTxHash());
        String agentName = "";
        if(rpcClientResult.isSuccess()){
            Map<String,Object> attr = (Map)rpcClientResult.getData();
            Integer status = Integer.parseInt(attr.get("status")+"");
            agentName = attr.get("agentName")+"";
            agentNode.setStatus(status);
        }
        if(StringUtils.isBlank(agentName)){
            agentName = agentNode.getAgentAddress();
        }
        AgentNodeDto agentNodeDto = new AgentNodeDto(agentNode,punishLogBusiness.getCountByStatus(EntityConstant.PUBLISH_YELLOW,address),agentName);
        result.setData(agentNodeDto);
        return result;

    }

    /**
     * 某个地址的个人共识信息
     * @param address
     * @return
     */
    @GET
    @Path("/address/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getAddressConsensus(@PathParam("address") String address){
        RpcClientResult result = null;
        if(!StringUtils.validAddress(address)){
            result = RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        HashMap<String,String> attr = new HashMap<>();
        //加载奖励
        attr.put("reward",addressRewardDetailBusiness.selectSumReward(address)+"");
        //AgentNode agentNode = agentNodeBusiness.getAgentByAddress(address);
        AgentDto agentDto = IndexContext.getNodeByAgentAddress(address);
        if(null != agentDto){
            attr.put("totalDeposit",agentDto.getTotalDeposit()+"");
            attr.put("packingAddress",agentDto.getPackingAddress());
        }else{
            attr.put("totalDeposit","0");
            attr.put("packingAddress","");
        }
        //加载可用余额和冻结余额
        Balance balance = balanceBusiness.getBalance(address);
        if(null != balance){
            attr.put("usable",balance.getUsable()+"");
            attr.put("locked",balance.getLocked()+"");
        }else{
            attr.put("usable","0");
            attr.put("locked","0");
        }
        //加载委托金额
        Long depositMoney = depositBusiness.selectTotalAmount(address);
        if(null == depositMoney){
            depositMoney = 0L;
        }
        attr.put("depositMoney",depositMoney+"");
        result.setData(attr);
        return result;
    }
}
