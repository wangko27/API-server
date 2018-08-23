package io.nuls.api.server.task;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.Balance;
import io.nuls.api.entity.Na;
import io.nuls.api.entity.NulsStatistics;
import io.nuls.api.server.business.BalanceBusiness;
import io.nuls.api.server.dto.AgentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    private static NulsStatistics nulsStatistics = NulsStatistics.getInstance();

    /**
     * 定时从数据库中将统计数据写入Ehcache缓存
     */
    public void execute() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Count Data Write Time>>>>>>>>>>>>>>>>"+(sdf.format(new Date())));



        List<AgentDto> agentNodeList = IndexContext.getAgentNodeList();
        //总节点数量
        nulsStatistics.setTotalNodes(agentNodeList.size());

        int consensusNumber = 0;
        for (AgentDto agent : agentNodeList){
            if(agent.getStatus() == EntityConstant.CONSENSUS_STATUS_CONSENSUSING){
                consensusNumber++;
            }
        }
        //共识节点数量
        nulsStatistics.setConsensusNodes(consensusNumber);

        //资产总量
        //实际流通量
        //商务合作持有量
        Balance balance = balanceBusiness.getBalance(BUSINESS_ADDRESS);
        Na business = Na.valueOf(balance.getUsable()).add(Na.valueOf(balance.getLocked()));
        nulsStatistics.setBusiness(business);
        //团队持有量
        //社区持有量
        //待映射总量
        //每日共识奖励 总额
        //委托总额
        //总交易次数

    }
}