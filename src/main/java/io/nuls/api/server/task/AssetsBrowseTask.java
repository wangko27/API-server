package io.nuls.api.server.task;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.HistoryContext;
import io.nuls.api.context.IndexContext;
import io.nuls.api.context.NulsContext;
import io.nuls.api.entity.*;
import io.nuls.api.server.business.BalanceBusiness;
import io.nuls.api.server.business.TransactionBusiness;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dao.util.EhcacheUtil;
import io.nuls.api.server.dto.AgentDto;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Flyglede on 2018/8/23.
 */
@Component
public class AssetsBrowseTask {

    /**
     * 商务合作账户地址
     */
    private static final String BUSINESS_ADDRESS = "Nse3uLgeCBWP48GCGh8L54gnELfpnSG9";

    /**
     * 团队持有账户地址
     */
    private static final String TEAM_ADDRESS = "Nse1vKX9QHF7A84MHqrh4rRubVHAPc18";

    /**
     * 社区基金账户地址
     */
    private static final String COMMUNITY_ADDRESS = "Nse9U8qaLcV7b6uFy3nr8pD3z7mKgo9u";

    /**
     * 映射地址1
     */
    private static final String MAPPING_ADDRESS_1 = "Nse2vWaSC6P14VQ4ykhdcj83ohdj63fd";

    /**
     * 映射地址2
     */
    private static final String MAPPING_ADDRESS_2 = "Nse6Qqteaid77Htn9S7vgW7guN4Q5MVs";

    @Autowired
    private BalanceBusiness balanceBusiness;

    @Autowired
    private TransactionBusiness transactionBusiness;

    @Autowired
    private UtxoLevelDbService utxoLevelDbService;

    private static NulsStatistics nulsStatistics = NulsStatistics.getInstance();

    /**
     * 定时从数据库中将统计数据写入Ehcache缓存
     */
    public void execute() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Count Data Write Time>>>>>>>>>>>>>>>>" + (sdf.format(new Date())));

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
        Balance balance = balanceBusiness.getBalance(BUSINESS_ADDRESS);
        Na business = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));
        nulsStatistics.setBusiness(business);

        //团队持有量
        balance = balanceBusiness.getBalance(TEAM_ADDRESS);
        Na team = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));
        nulsStatistics.setTeam(team);

        //社区持有量
        balance = balanceBusiness.getBalance(COMMUNITY_ADDRESS);
        Na community = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));
        nulsStatistics.setCommunity(community);

        //待映射总量
        balance = balanceBusiness.getBalance(MAPPING_ADDRESS_1);
        Na mapping1 = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));

        balance = balanceBusiness.getBalance(MAPPING_ADDRESS_2);
        Na mapping2 = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));

        nulsStatistics.setCommunity(mapping1.add(mapping2));

        //资产总量
        long amount = 0L;
        Cache cache = EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME);
        Map<Object, Element> map = cache.getAll(cache.getKeys());
        for (Element e : map.values()) {
            AddressHashIndex addressHashIndex = (AddressHashIndex) e.getObjectValue();
            Set<String> addrs = addressHashIndex.getHashIndexSet();
            for(String address : addrs){
                Utxo utxo = utxoLevelDbService.select(address);
                amount += utxo.getAmount().longValue();
            }
        }
        Na total = Na.valueOf(amount);
        nulsStatistics.setTotalAssets(total);

        //实际流通量=总量-商务合作余额-社区账户余额-团队账户余额
        Na circulation = total.minus(business).minus(team).minus(community);
        nulsStatistics.setCirculation(circulation);

        NulsContext.CacheNulsStatistics(Constant.TOKEN_CACHE_KEY, nulsStatistics);
    }
}