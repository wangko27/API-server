package io.nuls.api.server.task;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.BalanceListContext;
import io.nuls.api.context.HistoryContext;
import io.nuls.api.context.IndexContext;
import io.nuls.api.context.NulsContext;
import io.nuls.api.entity.*;
import io.nuls.api.server.business.BalanceBusiness;
import io.nuls.api.server.business.TransactionBusiness;
import io.nuls.api.server.dto.AgentDto;
import io.nuls.api.server.dto.UtxoDto;
import io.nuls.api.utils.PropertiesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Created by Flyglede on 2018/8/23.
 */
@Component
public class AssetsBrowseTask {

    /**
     * 商务合作账户地址
     */
    private String businessAddress = PropertiesUtils.readProperty(Constant.BUSINESS_ADDRESS);

    /**
     * 团队持有账户地址
     */
    private String teamAddress = PropertiesUtils.readProperty(Constant.TEAM_ADDRESS);

    /**
     * 社区基金账户地址
     */
    private String communityAddress = PropertiesUtils.readProperty(Constant.COMMUNITY_ADDRESS);

    /**
     * 映射地址(s)
     */
    private String[] mappingAddress = PropertiesUtils.readProperty(Constant.MAPPING_ADDRESS).split(",");


    @Autowired
    private BalanceBusiness balanceBusiness;

    @Autowired
    private TransactionBusiness transactionBusiness;

    private static NulsStatistics nulsStatistics = NulsStatistics.getInstance();

    @PostConstruct
    public void init() {
        businessAddress = PropertiesUtils.readProperty(Constant.BUSINESS_ADDRESS);
        teamAddress = PropertiesUtils.readProperty(Constant.TEAM_ADDRESS);
        communityAddress = PropertiesUtils.readProperty(Constant.COMMUNITY_ADDRESS);
        mappingAddress = PropertiesUtils.readProperty(Constant.MAPPING_ADDRESS).split(",");
    }

    /**
     * 定时从数据库中将统计数据写入Ehcache缓存
     */
    public void execute() {
        List<AgentDto> agentNodeList = IndexContext.getAgentNodeList();
        //总节点数量
        nulsStatistics.setTotalNodes(agentNodeList.size());

        int consensusNumber = 0;
        for (AgentDto agent : agentNodeList) {
            if (agent.getStatus() == EntityConstant.CONSENSUS_STATUS_CONSENSUSING) {
                consensusNumber++;
            }
        }
        //共识节点数量
        nulsStatistics.setConsensusNodes(consensusNumber);

        //每日共识奖励 总额
        nulsStatistics.setDailyReward(Na.valueOf(HistoryContext.rewardofday));

        //委托总额
        Map consensusData = IndexContext.getRpcConsensusData();
        Na totalDeposit = Na.valueOf(Long.valueOf(consensusData.get("totalDeposit") + ""));
        nulsStatistics.setDeposit(totalDeposit);

        //总交易次数
        nulsStatistics.setTrades(transactionBusiness.selectTotalCount());

        //商务合作持有量
        Balance balance = balanceBusiness.getBalance(businessAddress);
        Na business = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));
        nulsStatistics.setBusiness(business);

        //团队持有量
        balance = balanceBusiness.getBalance(teamAddress);
        Na team = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));
        nulsStatistics.setTeam(team);

        //社区持有量
        balance = balanceBusiness.getBalance(communityAddress);
        Na community = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));
        nulsStatistics.setCommunity(community);

        //待映射总量
        long maps = 0L;
        for(String addr : mappingAddress){
            balance = balanceBusiness.getBalance(addr.trim());
            maps += (balance.getUsable() + balance.getLocked());
        }
        Na unmapped = Na.valueOf(maps);

        nulsStatistics.setUnmapped(unmapped);

        //资产总量
        long total = 0L;
        List<UtxoDto> listUtxoDtos = BalanceListContext.getAllUtxoDtos();
        for (UtxoDto utxoDto : listUtxoDtos){
            total += utxoDto.getTotal();
        }
        nulsStatistics.setTotalAssets(total);

        //实际流通量=总量-商务合作余额-社区账户余额-团队账户余额
        long circulation = total - business.getValue() - team.getValue() - community.getValue();
        nulsStatistics.setCirculation(circulation);

        NulsContext.CacheNulsStatistics(Constant.TOKEN_CACHE_KEY, nulsStatistics);
    }
}