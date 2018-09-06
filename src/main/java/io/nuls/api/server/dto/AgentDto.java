package io.nuls.api.server.dto;

import io.nuls.api.constant.PocConsensusProtocolConstant;
import io.nuls.api.utils.LongUtils;

import java.util.LinkedHashMap;

/**
 * Description:
 * Author: moon
 * Date:  2018/8/2 0002
 */
public class AgentDto {
    private String agentHash;
    private String agentAddress;
    private String packingAddress;
    private String rewardAddress;
    private long deposit;
    private double commissionRate;
    private String agentName;
    private String agentId;
    private long time;
    private long blockHeight;
    private long delHeight;
    private int status;
    private double creditVal;
    private long totalDeposit;
    private String txHash;
    private int memberCount;

    public AgentDto(){

    }

    public AgentDto(LinkedHashMap link){
        this.agentHash = link.get("agentHash")+"";
        this.agentAddress = link.get("agentAddress")+"";
        this.packingAddress = link.get("packingAddress")+"";
        this.rewardAddress = link.get("rewardAddress")+"";
        this.deposit = Long.valueOf(link.get("deposit")+"");
        this.commissionRate = Double.parseDouble(link.get("commissionRate")+"");
        this.agentName = link.get("agentName")+"";
        this.agentId = link.get("agentId")+"";
        this.time = Long.valueOf(link.get("time")+"");
        this.blockHeight = Long.valueOf(link.get("blockHeight")+"");
        this.delHeight = Long.valueOf(link.get("delHeight")+"");
        this.status = Integer.valueOf(link.get("status")+"");
        this.creditVal = Double.parseDouble(link.get("creditVal")+"");
        this.totalDeposit = Long.valueOf(link.get("totalDeposit")+"");
        this.txHash = link.get("txHash")+"";
        this.memberCount = Integer.valueOf(link.get("memberCount")+"");
    }

    public AgentDto(String agentHash, String agentAddress, String packingAddress, String rewardAddress, long deposit, double commissionRate, String agentName, String agentId, long time, long blockHeight, long delHeight, int status, double creditVal, long totalDeposit, String txHash, int memberCount) {
        this.agentHash = agentHash;
        this.agentAddress = agentAddress;
        this.packingAddress = packingAddress;
        this.rewardAddress = rewardAddress;
        this.deposit = deposit;
        this.commissionRate = commissionRate;
        this.agentName = agentName;
        this.agentId = agentId;
        this.time = time;
        this.blockHeight = blockHeight;
        this.delHeight = delHeight;
        this.status = status;
        this.creditVal = creditVal;
        this.totalDeposit = totalDeposit;
        this.txHash = txHash;
        this.memberCount = memberCount;
    }

    public long getAvailableDepositAmount() {
        return LongUtils.sub(PocConsensusProtocolConstant.SUM_OF_DEPOSIT_OF_AGENT_UPPER_LIMIT.getValue(), this.getTotalDeposit());
    }
    public boolean canDeposit() {
        return getAvailableDepositAmount() >= PocConsensusProtocolConstant.ENTRUSTER_DEPOSIT_LOWER_LIMIT.getValue();
    }

    public String getAgentHash() {
        return agentHash;
    }

    public void setAgentHash(String agentHash) {
        this.agentHash = agentHash;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getPackingAddress() {
        return packingAddress;
    }

    public void setPackingAddress(String packingAddress) {
        this.packingAddress = packingAddress;
    }

    public String getRewardAddress() {
        return rewardAddress;
    }

    public void setRewardAddress(String rewardAddress) {
        this.rewardAddress = rewardAddress;
    }

    public long getDeposit() {
        return deposit;
    }

    public void setDeposit(long deposit) {
        this.deposit = deposit;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public long getDelHeight() {
        return delHeight;
    }

    public void setDelHeight(long delHeight) {
        this.delHeight = delHeight;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getCreditVal() {
        return creditVal;
    }

    public void setCreditVal(double creditVal) {
        this.creditVal = creditVal;
    }

    public long getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(long totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
