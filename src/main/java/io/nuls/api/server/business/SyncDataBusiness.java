package io.nuls.api.server.business;

import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.IndexContext;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.context.UtxoTempContext;
import io.nuls.api.entity.*;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SyncDataBusiness {

    @Autowired
    private BlockBusiness blockBusiness;
    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private TransactionRelationBusiness transactionRelationBusiness;
    @Autowired
    private AliasBusiness aliasBusiness;
    @Autowired
    private PunishLogBusiness punishLogBusiness;
    @Autowired
    private AddressRewardDetailBusiness detailBusiness;
    @Autowired
    private DepositBusiness depositBusiness;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;

    /**
     * 同步最新块数据
     * @param block
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void syncData(Block block) throws Exception {
        long time1=System.currentTimeMillis(),time2;
        try {
            blockBusiness.saveBlock(block.getHeader());
            /*缓存新块 首页数据展示用*/
            IndexContext.putBlock(block.getHeader());

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        /*list*/
        Map<String,Utxo> utxoMap = new HashMap<>();
        List<Utxo> utxoUpdateList = new ArrayList<>();
        List<Transaction> transactionList = new ArrayList<>();
        List<TransactionRelation> transactionRelationList = new ArrayList<>();
        List<AddressRewardDetail> addressRewardDetailList = new ArrayList<>();
        List<Alias> aliasList = new ArrayList<>();
        //List<AgentNode> agentNodeList = new ArrayList<>();
        List<Deposit> depositList = new ArrayList<>();
        List<PunishLog> punishLogList = new ArrayList<>();

        //int aj=0,ak=0,al=0;
        for (int i = 0; i < block.getTxList().size(); i++) {
            Transaction tx = block.getTxList().get(i);
            tx.setTxIndex(i);
            for (Utxo utxo : tx.getOutputs()) {
                utxoMap.put(utxo.getHashIndex(),utxo);
                //写入缓存
                UtxoContext.put(utxo.getAddress(),utxo.getHashIndex());
            }

            Map<String, Object> dataMap = new HashMap<>();
            utxoUpdateList.addAll(utxoBusiness.getListByFrom(tx,utxoMap));
            dataMap.put("scriptSign", tx.getScriptSign());
            dataMap.put("inputs", tx.getInputs());
            dataMap.put("outputs", tx.getOutputList());
            tx.setExtend(JSONUtils.obj2json(dataMap).getBytes());

            //transactionBusiness.save(tx);

            transactionList.add(tx);
            transactionRelationList.addAll(transactionRelationBusiness.getListByTx(tx));
            if (tx.getType() == EntityConstant.TX_TYPE_COINBASE) {
                addressRewardDetailList.addAll(detailBusiness.getRewardList(tx));
            } else if (tx.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS) {
                aliasList.add((Alias) tx.getTxData());
            } else if (tx.getType() == EntityConstant.TX_TYPE_REGISTER_AGENT) {
                //agentNodeList.add((AgentNode) tx.getTxData());
                agentNodeBusiness.save((AgentNode) tx.getTxData());
            } else if (tx.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS) {
                depositList.add((Deposit) tx.getTxData());
                depositBusiness.updateAgentNodeByDeposit((Deposit) tx.getTxData());
            } else if (tx.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT) {
                depositBusiness.cancelDeposit((Deposit) tx.getTxData(), tx.getHash());
            } else if (tx.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
                AgentNode agentNode = (AgentNode) tx.getTxData();
                agentNodeBusiness.stopAgent(agentNode, tx.getHash());
            } else if (tx.getType() == EntityConstant.TX_TYPE_RED_PUNISH) {
                punishLogList.add((PunishLog) tx.getTxData());
            } else if (tx.getType() == EntityConstant.TX_TYPE_YELLOW_PUNISH) {
                for (TxData data : tx.getTxDataList()) {
                    PunishLog log = (PunishLog) data;
                    punishLogList.add(log);
                }
            }
            /*缓存新交易*/
            IndexContext.putTransaction(tx);
        }
        utxoBusiness.saveAll(new ArrayList<>(utxoMap.values()));
        transactionBusiness.saveAll(transactionList);
        transactionRelationBusiness.saveAll(transactionRelationList);
        detailBusiness.saveAll(addressRewardDetailList);
        aliasBusiness.saveAll(aliasList);
        //agentNodeBusiness.saveAll(agentNodeList);
        punishLogBusiness.saveAll(punishLogList);
        depositBusiness.saveAll(depositList);
        utxoBusiness.updateAll(utxoUpdateList);
        time2 = System.currentTimeMillis();
        System.out.println("高度："+block.getHeader().getHeight()+"交易笔数："+transactionList.size()+"---保存耗时："+(time2-time1));
        utxoMap = null;
        utxoUpdateList= null;
        transactionList= null;
        transactionRelationList= null;
        addressRewardDetailList= null;
        aliasList= null;
        //agentNodeList= null;
        depositList= null;
        punishLogList= null;
    }

    /**
     * 回滚当前本地最新块
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollback(BlockHeader header) throws Exception {
        Log.error("--------------roll back, block height: " + header.getHeight() + ",hash :" + header.getHash());
        List<Transaction> txList = transactionBusiness.getList(header.getHeight());
        for (int i = txList.size() - 1; i >= 0; i--) {
            Transaction tx = txList.get(i);
            transactionBusiness.rollback(tx);
        }
        //回滚别名
        aliasBusiness.deleteByHeight(header.getHeight());
        //回滚惩罚记录
        punishLogBusiness.deleteByHeight(header.getHeight());
        //回滚奖励
        detailBusiness.deleteByHeight(header.getHeight());
        //回滚块
        blockBusiness.deleteByKey(header.getHeight());

        //回滚缓存
        for (int i = txList.size() - 1; i >= 0; i--) {
            Transaction tx = txList.get(i);
            if (tx.getOutputs() != null) {
                for (int j = tx.getOutputs().size() - 1; j >= 0; j--) {
                    Utxo tempUtxo = tx.getOutputs().get(j);
                    UtxoContext.remove(tempUtxo.getAddress());
                }
            }
            //UtxoKey utxoKey;
            Utxo utxo;
            if (tx.getInputs() != null) {
                for (int j = tx.getInputs().size() - 1; j >= 0; j--) {
                    Input input = tx.getInputs().get(j);
                    //utxoKey = new UtxoKey(input.getFromHash(), input.getFromIndex());
                    utxo = utxoBusiness.getByKey(input.getFromHash(), input.getFromIndex());
                    if (utxo != null) {
                        UtxoContext.put(utxo.getAddress(),utxo.getHashIndex());
                    }
                }
            }
        }
        //回滚最新块
        IndexContext.removeBlock(header);
        //重新加载最新的几条交易信息
        PageInfo<Transaction> pageInfo = transactionBusiness.getList(null,0,1,Constant.INDEX_TX_LIST_COUNT,3);
        if(null != pageInfo.getList()){
            IndexContext.initTransactions(pageInfo.getList());
        }
    }

}
