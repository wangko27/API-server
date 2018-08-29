package io.nuls.api.entity;

import java.math.BigDecimal;

public class AgentNode extends TxData {
    private String txHash;

    private String agentAddress;

    private String packingAddress;

    private String rewardAddress;

    private Long deposit;

    private BigDecimal commissionRate;

    private Long createTime;

    private Long blockHeight;

    private Integer status;

    private Long totalDeposit;

    private Integer depositCount;

    private BigDecimal creditValue;

    private Long totalPackingCount;

    private Long lastRewardHeight;

    private String deleteHash;

    private Long totalReward;

    /**
     * 不存入数据库，节点id，展示用，hash的最后八位
     */
    private String agendId;

    public String getAgendId() {
        return agendId;
    }

    public void setAgendId(String agendId) {
        this.agendId = agendId;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress == null ? null : agentAddress.trim();
    }

    public String getPackingAddress() {
        return packingAddress;
    }

    public void setPackingAddress(String packingAddress) {
        this.packingAddress = packingAddress == null ? null : packingAddress.trim();
    }

    public String getRewardAddress() {
        return rewardAddress;
    }

    public void setRewardAddress(String rewardAddress) {
        this.rewardAddress = rewardAddress == null ? null : rewardAddress.trim();
    }

    public Long getDeposit() {
        return deposit;
    }

    public void setDeposit(Long deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTotalDeposit() {
        if (totalDeposit == null) {
            totalDeposit = 0L;
        }
        return totalDeposit;
    }

    public void setTotalDeposit(Long totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public Integer getDepositCount() {
        if(depositCount == null) {
            depositCount = 0;
        }
        return depositCount;
    }

    public void setDepositCount(Integer depositCount) {
        this.depositCount = depositCount;
    }

    public BigDecimal getCreditValue() {
        return creditValue;
    }

    public void setCreditValue(BigDecimal creditValue) {
        this.creditValue = creditValue;
    }

    public Long getTotalPackingCount() {
        if(totalPackingCount == null) {
            totalPackingCount = 0L;
        }
        return totalPackingCount;
    }

    public void setTotalPackingCount(Long totalPackingCount) {
        this.totalPackingCount = totalPackingCount;
    }

    public Long getLastRewardHeight() {
        return lastRewardHeight;
    }

    public void setLastRewardHeight(Long lastRewardHeight) {
        this.lastRewardHeight = lastRewardHeight;
    }

    public String getDeleteHash() {
        return deleteHash;
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash;
    }

    public Long getTotalReward() {
        if(totalReward == null) {
            totalReward = 0L;
        }
        return totalReward;
    }

    public void setTotalReward(Long totalReward) {
        this.totalReward = totalReward;
    }
}