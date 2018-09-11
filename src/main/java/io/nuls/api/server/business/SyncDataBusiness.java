package io.nuls.api.server.business;

import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.IndexContext;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.*;
import io.nuls.api.model.ContractResultDto;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.WebwalletUtxoLevelDbService;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Autowired
    private WebwalletTransactionBusiness webwalletTransactionBusiness;
    @Autowired
    private ContractBusiness contractBusiness;
    @Autowired
    private SyncDataHandler syncDataHandler;

    private WebwalletUtxoLevelDbService webwalletUtxoLevelDbService = WebwalletUtxoLevelDbService.getInstance();
    private UtxoLevelDbService utxoLevelDbService = UtxoLevelDbService.getInstance();
    private RestFulUtils restFulUtils = RestFulUtils.getInstance();


    /**
     * 同步最新块数据
     *
     * @param block
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void syncData(Block block) throws Exception {
        long time1 = System.currentTimeMillis(), time2;
        /*list*/
        //存放区块内交易新生成的utxo
        Map<String, Utxo> utxoMap = new HashMap<>();
        //存放区块内交易引用到的utxo
        List<Utxo> fromList = new ArrayList<>();
        List<Transaction> txList = new ArrayList<>();
        List<TransactionRelation> txRelationList = new ArrayList<>();
        List<AddressRewardDetail> addressRewardDetailList = new ArrayList<>();
        List<Alias> aliasList = new ArrayList<>();
        List<Deposit> depositList = new ArrayList<>();
        List<PunishLog> punishLogList = new ArrayList<>();
        List<ContractDeleteInfo> deleteContractDataList = new ArrayList<>();
        List<ContractCallInfo> callContractDataList = new ArrayList<>();

        try {
            for (int i = 0; i < block.getTxList().size(); i++) {
                Transaction tx = block.getTxList().get(i);
                tx.setTxIndex(i);

                //存放新的utxo到utxoMap
                if (tx.getOutputs() != null && !tx.getOutputs().isEmpty()) {
                    for (Utxo utxo : tx.getOutputs()) {
                        utxoMap.put(utxo.getKey(), utxo);
                    }
                }
                //存放被花费的utxo
                fromList.addAll(utxoBusiness.getListByFrom(tx, utxoMap));

                txList.add(tx);
                //如果是红牌惩罚，不能放入relation，只能单独处理
                if(tx.getType()!=EntityConstant.TX_TYPE_RED_PUNISH){
                    txRelationList.addAll(transactionRelationBusiness.getListByTx(tx));
                }
                if (tx.getType() == EntityConstant.TX_TYPE_COINBASE) {
                    addressRewardDetailList.addAll(detailBusiness.getRewardList(tx));
                } else if (tx.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS) {
                    aliasList.add((Alias) tx.getTxData());
                } else if (tx.getType() == EntityConstant.TX_TYPE_REGISTER_AGENT) {
                    agentNodeBusiness.save((AgentNode) tx.getTxData());
                } else if (tx.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS) {
                    depositList.add((Deposit) tx.getTxData());
                    depositBusiness.updateAgentNodeByDeposit((Deposit) tx.getTxData());
                } else if (tx.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT) {
                    depositBusiness.cancelDeposit((Deposit) tx.getTxData(), tx.getHash());
                } else if (tx.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
                    agentNodeBusiness.stopAgent((AgentNode) tx.getTxData(), tx.getHash());
                } else if (tx.getType() == EntityConstant.TX_TYPE_RED_PUNISH) {
                    PunishLog punishLog = (PunishLog) tx.getTxData();
                    punishLogList.add(punishLog);
                    TransactionRelation key = new TransactionRelation(punishLog.getAddress(), tx.getHash(),tx.getType(),tx.getCreateTime());
                    txRelationList.add(key);
                    agentNodeBusiness.stopAgentByRedPublish(punishLog.getAddress(), tx.getHash());
                } else if (tx.getType() == EntityConstant.TX_TYPE_YELLOW_PUNISH) {
                    for (TxData data : tx.getTxDataList()) {
                        PunishLog log = (PunishLog) data;
                        punishLogList.add(log);
                        TransactionRelation key = new TransactionRelation(log.getAddress(), tx.getHash(),tx.getType(),tx.getCreateTime());
                        txRelationList.add(key);
                    }
                }else if(tx.getType() == EntityConstant.TX_TYPE_CREATE_CONTRACT){
                    //创建合约
                    ContractCreateInfo data = (ContractCreateInfo)tx.getTxData();
                    RpcClientResult result = restFulUtils.get("/contract/result/" + tx.getHash(), null);
//                    ContractResultDto aaa = restFulUtils.get("/contract/result/" + tx.getHash(), null, ContractResultDto.class);
                    if (result.isSuccess()) {
                        Object data1 = result.getData();
                        System.out.println(data1);
//                        System.out.println(aaa);
                    }
                }else if(tx.getType() == EntityConstant.TX_TYPE_CALL_CONTRACT){
                    //调用合约
                    ContractCallInfo data = (ContractCallInfo)tx.getTxData();
                    RpcClientResult result = restFulUtils.get("/contract/result/" + tx.getHash(), null);
                    if (result.isSuccess()) {
                        Object data1 = result.getData();
//                        contractBusiness.deleteContract(data.getContractAddress());
                    }
                    callContractDataList.add(data);
                }else if(tx.getType() == EntityConstant.TX_TYPE_DELETE_CONTRACT){
                    //删除合约
                    ContractDeleteInfo data = (ContractDeleteInfo)tx.getTxData();
                    RpcClientResult result = restFulUtils.get("/contract/result/" + tx.getHash(), null);
                    if (result.isSuccess()) {
                        Object data1 = result.getData();
                        contractBusiness.deleteContract(data.getContractAddress());
                    }
                    deleteContractDataList.add(data);
                }else if(tx.getType() == EntityConstant.TX_TYPE_CONTRACT_TRANSFER){
                    //合约转账
                    System.out.println("合约转账");
                    System.out.println("tx.getData:"+tx.getTxData());
                }
            }

            blockBusiness.save(block.getHeader());
            transactionBusiness.saveAll(txList);
            webwalletTransactionBusiness.deleteStatusByList(txList);
            transactionRelationBusiness.saveAll(txRelationList);
            utxoBusiness.saveAll(utxoMap);
            utxoBusiness.updateAll(fromList);

            detailBusiness.saveAll(addressRewardDetailList);
            aliasBusiness.saveAll(aliasList);
            punishLogBusiness.saveAll(punishLogList);
            depositBusiness.saveAll(depositList);
            contractBusiness.saveAllDelInfo(deleteContractDataList);
            contractBusiness.saveAllCallInfo(callContractDataList);

            //为了让存入leveldb更快，这里直接做map，全部处理完成之后，再存入leveldb
            Map<String,AddressHashIndex> attrMapList = new HashMap<>();
            //已经花费了的
            for (Utxo utxo : fromList) {
                AddressHashIndex addressHashIndex = null;
                if(attrMapList.containsKey(utxo.getAddress())){
                    //已经存在，直接移除
                    attrMapList.get(utxo.getAddress()).getHashIndexSet().remove(utxo.getKey());
                }else{
                    //不存在，新建一个，去leveldb获取数据，然后删除
                    addressHashIndex = new AddressHashIndex();
                    addressHashIndex.setAddress(utxo.getAddress());
                    Set<String> setList = UtxoContext.get(utxo.getAddress());
                    setList.remove(utxo.getKey());
                    addressHashIndex.setHashIndexSet(setList);
                    attrMapList.put(utxo.getAddress(),addressHashIndex);
                }
            }
            //新的未花费
            for (Utxo utxo : utxoMap.values()) {
                if (utxo.getSpendTxHash() == null) {
                    AddressHashIndex addressHashIndex = null;
                    if(attrMapList.containsKey(utxo.getAddress())){
                        //已经存在，直接再新增一个
                        attrMapList.get(utxo.getAddress()).getHashIndexSet().add(utxo.getKey());
                    }else{
                        //不存在，新建一个，去leveldb获取数据
                        addressHashIndex = new AddressHashIndex();
                        addressHashIndex.setAddress(utxo.getAddress());
                        Set<String> setList = UtxoContext.get(utxo.getAddress());
                        setList.add(utxo.getKey());
                        addressHashIndex.setHashIndexSet(setList);
                        attrMapList.put(utxo.getAddress(),addressHashIndex);
                    }
                }
            }
            UtxoContext.putMap(attrMapList);
            attrMapList = null;

            //所有修改缓存的需要等数据库的保存成功后，再做修改，避免回滚
            //缓存新块 首页数据展示用
            IndexContext.putBlock(block.getHeader());
            //缓存新交易
            int end = txList.size();
            int start = 0;
            if (end > Constant.INDEX_TX_LIST_COUNT) {
                start = end - Constant.INDEX_TX_LIST_COUNT;
            }
            for (int i = start; i < end; i++) {
                IndexContext.putTransaction(txList.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        time2 = System.currentTimeMillis();
        System.out.println("高度：" + block.getHeader().getHeight() + "---交易笔数：" + txList.size() + "---保存耗时：" + (time2 - time1));
        utxoMap = null;
        fromList = null;
        txList = null;
        txRelationList = null;
        addressRewardDetailList = null;
        aliasList = null;
        depositList = null;
        punishLogList = null;
    }

    /**
     * 回滚当前本地最新块
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollback(BlockHeader header) throws Exception {
        try {
            Log.warn("--------------roll back, block height: " + header.getHeight() + ",hash :" + header.getHash());

            List<Transaction> txList = transactionBusiness.getList(header);   //回滚的交易
            List<Utxo> outputs = new ArrayList<>();                           //回滚时需要删除output
            Map<String, Utxo> inputMap = new HashMap<>();                     //回滚时需要重置的inputs

            Utxo utxo;
            for (int i = txList.size() - 1; i >= 0; i--) {
                Transaction tx = txList.get(i);
                if (tx.getOutputList() != null && !tx.getOutputList().isEmpty()) {
                    for (Output output : tx.getOutputList()) {
                        utxo = utxoLevelDbService.select(output.getKey());
                        outputs.add(utxo);
                        if (inputMap.containsKey(utxo.getKey())) {
                            inputMap.remove(utxo.getKey());
                        }
                    }
                }
                if (tx.getInputs() != null && !tx.getInputs().isEmpty()) {
                    for (Input input : tx.getInputs()) {
                        utxo = utxoLevelDbService.select(input.getKey());
                        utxo.setSpendTxHash(null);
                        inputMap.put(utxo.getKey(), utxo);
                    }
                }
                transactionBusiness.rollback(tx);
            }
            //回滚别名
            aliasBusiness.deleteByHeight(header.getHeight());
            //回滚惩罚记录
            punishLogBusiness.deleteByHeight(header.getHeight());
            //回滚奖励
            detailBusiness.deleteByHeight(header.getHeight());
            //回滚交易
            transactionBusiness.deleteList(header.getTxHashList());
            //回滚交易关系
            transactionRelationBusiness.deleteList(header.getTxHashList());
            //回滚块
            blockBusiness.deleteByKey(header.getHeight());
            //回滚levelDB与缓存
            //回滚交易
            transactionBusiness.deleteLevelDBList(header.getTxHashList());
            //回滚utxo
            utxoBusiness.rollbackByTo(outputs);
            utxoBusiness.rollBackByFrom(inputMap);
            //回滚最新块
            IndexContext.removeBlock(header);
            //重新加载最新的几条交易信息
            PageInfo<Transaction> pageInfo = transactionBusiness.getNewestList(10);
            if (null != pageInfo.getList()) {
                IndexContext.initTransactions(pageInfo.getList());
            }
            //回滚块时，删除所有临时utxo
            webwalletUtxoLevelDbService.deleteAll();

            //回滚删除合约交易
            contractBusiness.rollbackContractDeleteInfo(header.getHash());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
