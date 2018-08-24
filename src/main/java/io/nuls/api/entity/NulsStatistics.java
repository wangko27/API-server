package io.nuls.api.entity;

/**
 * 全网token统计
 * NULS global token statistics
 *
 * @author: Charlie
 * @date: 2018/8/23
 */
public class NulsStatistics {

    private static final NulsStatistics INSTANCE = new NulsStatistics();

    public static NulsStatistics getInstance(){
        return INSTANCE;
    }

    /**
     * 资产总量
     * Total token
     */
    private transient Na totalAssets;

    /**
     * 实际流通量
     * The actual circulation amount of token
     */
    private Na circulation;

    /**
     * 商务合作持有量
     * The balance of tokens used for business cooperation
     *
     */
    private Na business;

    /**
     * 团队持有量
     * The balance of tokens for team
     */
    private Na team;

    /**
     * 社区持有量
     * The balance of tokens for community
     */
    private Na community;

    /**
     * 待映射总量
     */
    private Na unmapped;

    /**
     * 每日共识奖励 总额
     * Daily consensus reward amount
     */
    private Na dailyReward;

    /**
     * 委托总额
     * The total number of deposit
     */
    private Na deposit;

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


    private NulsStatistics(){

    }

    public Na getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(Na totalAssets) {
        this.totalAssets = totalAssets;
    }

    public Na getCirculation() {
        return circulation;
    }

    public void setCirculation(Na circulation) {
        this.circulation = circulation;
    }

    public Na getBusiness() {
        return business;
    }

    public void setBusiness(Na business) {
        this.business = business;
    }

    public Na getTeam() {
        return team;
    }

    public void setTeam(Na team) {
        this.team = team;
    }

    public Na getCommunity() {
        return community;
    }

    public void setCommunity(Na community) {
        this.community = community;
    }

    public Na getUnmapped() {
        return unmapped;
    }

    public void setUnmapped(Na unmapped) {
        this.unmapped = unmapped;
    }

    public Na getDailyReward() {
        return dailyReward;
    }

    public void setDailyReward(Na dailyReward) {
        this.dailyReward = dailyReward;
    }

    public Na getDeposit() {
        return deposit;
    }

    public void setDeposit(Na deposit) {
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
