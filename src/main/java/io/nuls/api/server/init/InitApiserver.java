package io.nuls.api.server.init;

import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.context.*;
import io.nuls.api.entity.*;
import io.nuls.api.server.business.*;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.UtxoDto;
import io.nuls.api.server.leveldb.manager.LevelDBManager;
import io.nuls.api.server.leveldb.service.DBService;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private DBService dbService;

    @PostConstruct
    public void init() {
        /*List<Utxo> list = UtxoLevelDbService.getInstance().getList();
        int nullcount = 0;
        for(Utxo utxo: list){
            if(null == utxo){
                nullcount++;
            }
        }
        System.out.println("初始化---utxo数量："+ list.size()+",null数量："+nullcount);*/

        try {
            LevelDBManager.init();
            //utxo
            dbService.createArea(Constant.UTXO_DB_NAME);
            //transaction
            dbService.createArea(Constant.TRANSACTION_DB_NAME);
            //blockheader
            dbService.createArea(Constant.BLOCKHEADER_DB_NAME);
        } catch (Exception e) {
            //skip it
        }

        /*加载utxo*/
        utxoBusiness.initUtxoList();

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
        Long rewardOfDay = blockBusiness.getBlockSumRewardByTime(new Date().getTime());
        HistoryContext.rewardofday = rewardOfDay == null ? 0L : rewardOfDay;

        /*加载别名到缓存*/
        //AliasContext
        List<Alias> aliasList = aliasBusiness.getList();
        for (Alias alias : aliasList) {
            AliasContext.put(alias);
        }

        /*加载初始化的区块列表*/
        PageInfo<BlockHeader> blockHeaderPageInfo = blockBusiness.getList(null, 1, Constant.INDEX_BLOCK_LIST_COUNT);
        List<BlockHeader> blockList = blockHeaderPageInfo.getList();
        if (null != blockList) {
            IndexContext.initBlocks(blockList);
        }

        /*加载初始化的交易列表*/
        PageInfo<Transaction> pageInfo = transactionBusiness.getList(null, 0, 1, Constant.INDEX_TX_LIST_COUNT, 3);
        if (null != pageInfo.getList()) {
            IndexContext.initTransactions(pageInfo.getList());
        }

        /*初始化共识信息*/
        RpcClientResult rpcClientResult = agentNodeBusiness.getConsensus();
        if (rpcClientResult.isSuccess()) {
            IndexContext.resetRpcConsensusData((Map) rpcClientResult.getData());
        }

    }
}
