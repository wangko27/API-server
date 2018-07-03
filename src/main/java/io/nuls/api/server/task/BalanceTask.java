package io.nuls.api.server.task;

import io.nuls.api.context.BalanceListContext;
import io.nuls.api.context.HistoryContext;
import io.nuls.api.context.PackingAddressContext;
import io.nuls.api.server.business.AgentNodeBusiness;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.UtxoBusiness;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.UtxoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 统计出块排行和持币账户排行，现在每一个小时统计一次
 */
@Component
public class BalanceTask {

    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;
    @Autowired
    private BlockBusiness blockBusiness;

    public void execute() {
        /*加载持币账户排行榜*/
        List<UtxoDto> blockDtoList = utxoBusiness.getBlockSumTxamount();
        BalanceListContext.reset(blockDtoList);

        /*加载出块账户排行榜*/
        List<AgentNodeDto> agentNodeDtoList = agentNodeBusiness.selectTotalpackingCount();
        PackingAddressContext.reset(agentNodeDtoList);

        /*加载24小时奖励*/
        Long rewardOfDay = blockBusiness.getBlockSumRewardByTime(new Date().getTime());
        HistoryContext.rewardofday = rewardOfDay==null?0L:rewardOfDay;
    }
}
