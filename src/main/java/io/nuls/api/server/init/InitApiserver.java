package io.nuls.api.server.init;

import io.nuls.api.context.BalanceListContext;
import io.nuls.api.context.PackingAddressContext;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.business.AgentNodeBusiness;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.UtxoBusiness;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.UtxoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Description: 初始化 第一次启动的时候加载一些信息
 * Author: zsj
 * Date:  2018/6/5 0005
 */
@Service
public class InitApiserver {
    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;
    @Autowired
    private BlockBusiness blockBusiness;

    @PostConstruct
    public void init() {
        /*加载utxo*/
        List<Utxo> utxoList = utxoBusiness.getList(null,2);
        for(Utxo utxo:utxoList){
            UtxoContext.put(utxo);
        }
        /*启动*/
        /*加载14天历史*/
        blockBusiness.initHistory();
        /*加载持币账户排行榜*/
        List<UtxoDto> blockDtoList = utxoBusiness.getBlockSumTxamount();
        BalanceListContext.reset(blockDtoList);
        /*加载出块账户排行榜*/
        List<AgentNodeDto> agentNodeDtoList = agentNodeBusiness.selectTotalpackingCount();
        PackingAddressContext.reset(agentNodeDtoList);
    }
}
