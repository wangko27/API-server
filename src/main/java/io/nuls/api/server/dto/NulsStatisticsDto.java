package io.nuls.api.server.dto;

import io.nuls.api.entity.Na;
import io.nuls.api.entity.NulsStatistics;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author: Charlie
 * @date: 2018/8/23
 */
public class NulsStatisticsDto {

    /**
     * 资产总量
     * Total token
     */
    private double totalAssets;

    /**
     * 实际流通量
     * The actual circulation amount of token
     */
    private double circulation;

    /**
     * 商务合作持有量
     * The balance of tokens used for business cooperation
     *
     */
    private double business;

    /**
     * 团队持有量
     * The balance of tokens for team
     */
    private double team;

    /**
     * 社区持有量
     * The balance of tokens for community
     */
    private double community;

    /**
     * 待映射总量
     */
    private double unmapped;

    /**
     * 每日共识奖励 总额
     * Daily consensus reward amount
     */
    private double dailyReward;

    /**
     * 委托总额
     * The total number of deposit
     */
    private double deposit;

    /**
     * 交易次数
     * Number of Trades
     */
    private long trades;

    /**
     * 节点总数
     * The total number of nodes
     */
    private int totalNodes;

    /**
     * 共识节点总数
     * The total number of consensus nodes
     */
    private int consensusNodes;

    public NulsStatisticsDto(NulsStatistics ts){

        this.totalAssets = new BigDecimal(ts.getTotalAssets())
                .movePointLeft(Na.SMALLEST_UNIT_EXPONENT)
                .setScale(Na.SMALLEST_UNIT_EXPONENT, RoundingMode.HALF_DOWN).doubleValue();
        this.circulation = new BigDecimal(ts.getCirculation())
                .movePointLeft(Na.SMALLEST_UNIT_EXPONENT)
                .setScale(Na.SMALLEST_UNIT_EXPONENT, RoundingMode.HALF_DOWN).doubleValue();
        this.business = ts.getBusiness().toDouble();
        this.team = ts.getTeam().toDouble();
        this.community = ts.getCommunity().toDouble();
        this.unmapped = ts.getUnmapped().toDouble();
        this.dailyReward = ts.getDailyReward().toDouble();
        this.deposit = ts.getDeposit().toDouble();
        this.trades = ts.getTrades();
        this.totalNodes = ts.getTotalNodes();
        this.consensusNodes = ts.getConsensusNodes();
    }

    public NulsStatisticsDto(){

    }

    public double getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(double totalAssets) {
        this.totalAssets = totalAssets;
    }

    public double getCirculation() {
        return circulation;
    }

    public void setCirculation(double circulation) {
        this.circulation = circulation;
    }

    public double getBusiness() {
        return business;
    }

    public void setBusiness(double business) {
        this.business = business;
    }

    public double getTeam() {
        return team;
    }

    public void setTeam(double team) {
        this.team = team;
    }

    public double getCommunity() {
        return community;
    }

    public void setCommunity(double community) {
        this.community = community;
    }

    public double getUnmapped() {
        return unmapped;
    }

    public void setUnmapped(double unmapped) {
        this.unmapped = unmapped;
    }

    public double getDailyReward() {
        return dailyReward;
    }

    public void setDailyReward(double dailyReward) {
        this.dailyReward = dailyReward;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public long getTrades() {
        return trades;
    }

    public void setTrades(long trades) {
        this.trades = trades;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    public int getConsensusNodes() {
        return consensusNodes;
    }

    public void setConsensusNodes(int consensusNodes) {
        this.consensusNodes = consensusNodes;
    }
}
