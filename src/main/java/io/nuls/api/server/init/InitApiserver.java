package io.nuls.api.server.init;

import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.context.*;
import io.nuls.api.entity.Alias;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Transaction;
import io.nuls.api.server.business.*;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.UtxoDto;
import io.nuls.api.utils.TimeService;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Description: 初始化 第一次启动的时候加载一些信息
 * Author: moon
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
    private AliasBusiness aliasBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;

    //private AddressHashIndexLevelDbService addressHashIndexLevelDbService = AddressHashIndexLevelDbService.getInstance();

    @PostConstruct
    public void init() {

        //UtxoContext.initCache(addressHashIndexLevelDbService.getAll());

        /*启动*/
        Log.info("--------启动类加载开始-------");
        long time = TimeService.currentTimeMillis(),time2;
        /*加载14天历史*/
        blockBusiness.initHistory(1);
        time2 = TimeService.currentTimeMillis();
        Log.info("14天交易历史加载完成，耗时："+(time2-time));
        /*加载持币账户排行榜*/
        if(null == BalanceListContext.getAllUtxoDtos()){
            List<UtxoDto> blockDtoList = utxoBusiness.getBlockSumTxamount();
            BalanceListContext.reset(blockDtoList);
        }
        time = TimeService.currentTimeMillis();
        Log.info("持币账户排行榜加载完成，耗时："+(time-time2));
        /*加载出块账户排行榜*/
        List<AgentNodeDto> agentNodeDtoList = agentNodeBusiness.selectTotalpackingCount();
        PackingAddressContext.reset(agentNodeDtoList);
        time2 = TimeService.currentTimeMillis();
        Log.info("出块账户排行榜加载完成，耗时："+(time2-time));

        /*加载24小时奖励*/
        Long rewardOfDay = blockBusiness.getBlockSumRewardByTime(TimeService.currentTimeMillis());
        HistoryContext.rewardofday = rewardOfDay == null ? 0L : rewardOfDay;
        time = TimeService.currentTimeMillis();
        Log.info("24小时奖励加载完成，耗时："+(time-time2));
        /*加载别名到缓存*/
        //AliasContext
        List<Alias> aliasList = aliasBusiness.getList();
        for (Alias alias : aliasList) {
            AliasContext.put(alias);
        }
        time2 = TimeService.currentTimeMillis();
        Log.info("别名加载完成，耗时："+(time2-time));
        /*加载初始化的区块列表*/
        PageInfo<BlockHeader> blockHeaderPageInfo = blockBusiness.getList(null, 1, Constant.INDEX_BLOCK_LIST_COUNT);
        List<BlockHeader> blockList = blockHeaderPageInfo.getList();
        if (null != blockList) {
            IndexContext.initBlocks(blockList);
        }
        time = TimeService.currentTimeMillis();
        Log.info("初始化的区块列表加载完成，耗时："+(time-time2));
        /*加载初始化的交易列表*/
        PageInfo<Transaction> pageInfo = transactionBusiness.getList(null, 0, 1, Constant.INDEX_TX_LIST_COUNT, 3);
        if (null != pageInfo.getList()) {
            IndexContext.initTransactions(pageInfo.getList());
        }
        time2 = TimeService.currentTimeMillis();
        Log.info("初始化的交易列表加载完成，耗时："+(time2-time));
        /*初始化共识信息*/
        RpcClientResult rpcClientResult = agentNodeBusiness.getConsensus();
        if (rpcClientResult.isSuccess()) {
            IndexContext.resetRpcConsensusData((Map) rpcClientResult.getData());
        }
        time = TimeService.currentTimeMillis();
        Log.info("共识信息加载完成，耗时："+(time-time2));

    }
}
