package io.nuls.api.server.init;

import io.nuls.api.context.*;
import io.nuls.api.entity.Alias;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.business.*;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.UtxoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;
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
    @Autowired
    private AddressRewardDetailBusiness addressRewardDetailBusiness;
    @Autowired
    private AliasBusiness aliasBusiness;

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

        /*加载24小时奖励*/
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long time = cal.getTime().getTime();
        Long rewardOfDay = addressRewardDetailBusiness.selectDayofReward(time);
        HistoryContext.rewardofday = rewardOfDay==null?0L:rewardOfDay;

        /*加载别名到缓存*/
        //AliasContext
        List<Alias> aliasList = aliasBusiness.getList();
        for(Alias alias : aliasList){
            AliasContext.put(alias);
        }
    }
}
