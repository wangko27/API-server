package io.nuls.api.server.task;

import io.nuls.api.context.BalanceListContext;
import io.nuls.api.context.PackingAddressContext;
import io.nuls.api.server.business.AgentNodeBusiness;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.UtxoBusiness;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.UtxoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 统计，每天凌晨12点统计一次
 * Author: zsj
 * Date:  2018/6/5 0005
 */
@Component
public class TransactionHistoryTask {

    @Autowired
    private BlockBusiness blockBusiness;
    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;

    public void execute(){
        /*14天交易历史*/
        blockBusiness.initHistory();
        /*加载持币账户排行榜*/
        List<UtxoDto> blockDtoList = utxoBusiness.getBlockSumTxamount();
        BalanceListContext.reset(blockDtoList);

        /*加载出块账户排行榜*/
        List<AgentNodeDto> agentNodeDtoList = agentNodeBusiness.selectTotalpackingCount();
        PackingAddressContext.reset(agentNodeDtoList);

    }
}
