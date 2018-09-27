package io.nuls.api.server.business;

import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.constant.ContractConstant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.IndexContext;
import io.nuls.api.context.NulsContext;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.AddressHashIndex;
import io.nuls.api.entity.AddressRewardDetail;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.Alias;
import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.entity.ContractCallInfo;
import io.nuls.api.entity.ContractCreateInfo;
import io.nuls.api.entity.ContractDeleteInfo;
import io.nuls.api.entity.ContractResultInfo;
import io.nuls.api.entity.ContractTokenInfo;
import io.nuls.api.entity.ContractTokenTransferInfo;
import io.nuls.api.entity.ContractTransaction;
import io.nuls.api.entity.ContractTransferInfo;
import io.nuls.api.entity.Deposit;
import io.nuls.api.entity.Input;
import io.nuls.api.entity.Output;
import io.nuls.api.entity.PunishLog;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.TransactionRelation;
import io.nuls.api.entity.TxData;
import io.nuls.api.entity.Utxo;
import io.nuls.api.model.BlockExtendsData;
import io.nuls.api.model.ContractTokenTransferDto;
import io.nuls.api.server.dao.mapper.leveldb.ProtocolLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.WebwalletUtxoLevelDbService;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private ContractAddressBusiness contractAddressBusiness;
    @Autowired
    private ContractCreateBusiness contractCreateBusiness;
    @Autowired
    private SyncDataHandler syncDataHandler;

    private WebwalletUtxoLevelDbService webwalletUtxoLevelDbService = WebwalletUtxoLevelDbService.getInstance();
    private UtxoLevelDbService utxoLevelDbService = UtxoLevelDbService.getInstance();
    private ProtocolLevelDbService protocolLevelDbService = ProtocolLevelDbService.getInstance();
    private RestFulUtils restFulUtils = RestFulUtils.getInstance();


    /**
     * 同步最新块数据
     *
     * @param block
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void syncData(Block block) throws Exception {

        //新增代码，判断主网协议版本号，版本号升级到2以后，交易hash的生成方式会改变
        checkNulsProtocolUpgrade(block.getHeader());

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
        List<ContractCreateInfo> contractCreateDataList = new ArrayList<>();
        List<ContractAddressInfo> contractAddressList = new ArrayList<>();
        List<ContractDeleteInfo> deleteContractDataList = new ArrayList<>();
        List<ContractCallInfo> callContractDataList = new ArrayList<>();
        List<ContractResultInfo> contractResultInfoList = new ArrayList<>();
        List<ContractTokenInfo> contractTokenInfoList = new ArrayList<>();
        List<ContractTransaction> contractTransactionList = new ArrayList<>();
        List<ContractTokenTransferInfo> contractTokenTransferInfoList = new ArrayList<>();
        List<ContractTransferInfo> contractTransferInfoList = new ArrayList<>();

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
                if (tx.getType() != EntityConstant.TX_TYPE_RED_PUNISH) {
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
                    TransactionRelation key = new TransactionRelation(punishLog.getAddress(), tx.getHash(), tx.getType(), tx.getCreateTime());
                    txRelationList.add(key);
                    agentNodeBusiness.stopAgentByRedPublish(punishLog.getAddress(), tx.getHash());
                } else if (tx.getType() == EntityConstant.TX_TYPE_YELLOW_PUNISH) {
                    for (TxData data : tx.getTxDataList()) {
                        PunishLog log = (PunishLog) data;
                        punishLogList.add(log);
                        TransactionRelation key = new TransactionRelation(log.getAddress(), tx.getHash(), tx.getType(), tx.getCreateTime());
                        txRelationList.add(key);
                    }
                } else if (tx.getType() == EntityConstant.TX_TYPE_CREATE_CONTRACT || tx.getType() == EntityConstant.TX_TYPE_CALL_CONTRACT || tx.getType() == EntityConstant.TX_TYPE_DELETE_CONTRACT) {
                    //查询合约交易执行结果
                    RpcClientResult<ContractResultInfo> result = syncDataHandler.getContractResult(tx.getHash());
                    if (result.isFailed() || result.getData() == null) {
                        continue;
                    }

                    ContractResultInfo resultData = result.getData();
                    resultData.setTxHash(tx.getHash());

                    //保存合约交易记录（创建、调用、删除合约，不包含内部转账）
                    ContractTransaction contractTransaction = new ContractTransaction();
                    contractTransaction.setTxHash(tx.getHash());
                    contractTransaction.setTxType(tx.getType());
                    contractTransaction.setStatus(ContractConstant.CONTRACT_STATUS_CONFIRMED);
                    contractTransaction.setCreateTime(tx.getCreateTime());
                    contractTransaction.setContractAddress(resultData.getContractAddress());

                    //ContractResultInfo中有代币转账，需要分别进行处理
                    //代币转账
                    String tokenTransfersString = resultData.getTokenTransfers();
                    if (StringUtils.isNotBlank(tokenTransfersString)) {
                        List<ContractTokenTransferDto> contractTokenTransferDtos = JSONUtils.json2list(tokenTransfersString, ContractTokenTransferDto.class);
                        for (ContractTokenTransferDto contractTokenTransferDto : contractTokenTransferDtos) {
                            ContractTokenTransferInfo contractTokenTransferInfo = new ContractTokenTransferInfo(contractTokenTransferDto);
                            contractTokenTransferInfo.setCreateTxHash(tx.getHash());
                            contractTokenTransferInfo.setTxHash(StringUtils.getNewUUID());
                            contractTokenTransferInfo.setContractAddress(resultData.getContractAddress());
                            contractTokenTransferInfo.setCreateTime(tx.getCreateTime());
                            contractTokenTransferInfoList.add(contractTokenTransferInfo);
                        }
                        contractBusiness.calContractTokenAssets(contractTokenTransferInfoList, resultData.getContractAddress(), false);
                    }

                    if (tx.getType() == EntityConstant.TX_TYPE_CREATE_CONTRACT) {
                        //创建合约
                        if (tx.getTxData() != null && ContractConstant.CONTRACT_STATUS_SUCCESS.equals(resultData.getSuccess())) {
                            ContractCreateInfo contractCreateData = (ContractCreateInfo) tx.getTxData();
                            contractCreateData.setCreateTxHash(tx.getHash());
                            //查询合约地址信息
                            RpcClientResult<ContractAddressInfo> contractInfoResult = syncDataHandler.getContractInfo(contractCreateData.getContractAddress());
                            ContractAddressInfo contractAddressInfo = contractInfoResult.getData();
                            if (contractAddressInfo != null) {
                                //设置合约包含方法
                                contractCreateData.setMethods(contractAddressInfo.getMethods());
                                contractCreateData.setCreateTime(contractAddressInfo.getCreateTime());
                                contractAddressList.add(contractAddressInfo);

                                //NRC20代币信息
                                if (StringUtils.isNotBlank(contractAddressInfo.getTokenName())) {
                                    ContractTokenInfo contractTokenInfo = new ContractTokenInfo(contractAddressInfo);
                                    contractTokenInfoList.add(contractTokenInfo);
                                }
                                //设置合约交易记录创建者
                                contractTransaction.setCreater(contractAddressInfo.getCreater());
                            }
                            contractCreateDataList.add(contractCreateData);
                        }
                    } else if (tx.getType() == EntityConstant.TX_TYPE_CALL_CONTRACT) {
                        //调用合约
                        ContractCallInfo data = (ContractCallInfo) tx.getTxData();
                        data.setCreateTxHash(tx.getHash());

                        //设置合约交易记录创建者
                        contractTransaction.setCreater(data.getCreater());
                        callContractDataList.add(data);
                    } else if (tx.getType() == EntityConstant.TX_TYPE_DELETE_CONTRACT) {
                        //删除合约
                        ContractDeleteInfo data = (ContractDeleteInfo) tx.getTxData();
                        data.setCreateTxHash(tx.getHash());
                        if (result.isSuccess()) {
                            contractBusiness.deleteContract(data);
                        }
                        //设置合约交易记录创建者
                        contractTransaction.setCreater(data.getCreater());
                        deleteContractDataList.add(data);
                    }
                    if (ContractConstant.CONTRACT_STATUS_SUCCESS.equals(resultData.getSuccess()) && StringUtils.isNotBlank(resultData.getContractAddress())) {
                        //合约交易记录
                        contractTransactionList.add(contractTransaction);
                        //合约调用结果
                        contractResultInfoList.add(resultData);
                    }
                } else if (tx.getType() == EntityConstant.TX_TYPE_CONTRACT_TRANSFER) {
                    //合约内部转账
                    ContractTransferInfo transferInfo = (ContractTransferInfo) tx.getTxData();
                    transferInfo.setTxHash(tx.getHash());
                    transferInfo.setCreateTime(tx.getCreateTime());
                    transferInfo.setFromAddress(transferInfo.getContractAddress());
                    if (tx.getOutputList() != null && tx.getOutputList().size() > 0) {
                        transferInfo.setToAddress(tx.getOutputList().get(0).getAddress());
                        transferInfo.setTxValue(tx.getOutputList().get(0).getValue());
                    }
                    //合约内部转账交易记录
                    contractTransferInfoList.add(transferInfo);
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

            //智能合约相关数据保存
            contractBusiness.saveAllDelInfo(deleteContractDataList);
            contractBusiness.saveAllCallInfo(callContractDataList);
            contractBusiness.saveAllContractResult(contractResultInfoList);
            contractBusiness.saveAllToken(contractTokenInfoList);
            contractBusiness.saveAllTransaction(contractTransactionList);
            contractBusiness.saveAllTokenTransferInfo(contractTokenTransferInfoList);
            contractBusiness.saveAllTransferInfo(contractTransferInfoList);
            //智能合约地址批量保存
            contractBusiness.saveAllContractAddress(contractAddressList);
            //智能合约创建交易数据批量保存
            contractBusiness.saveAllCreateData(contractCreateDataList);

            //为了让存入leveldb更快，这里直接做map，全部处理完成之后，再存入leveldb
            Map<String, AddressHashIndex> attrMapList = new HashMap<>();
            //已经花费了的
            for (Utxo utxo : fromList) {
                AddressHashIndex addressHashIndex = null;
                if (attrMapList.containsKey(utxo.getAddress())) {
                    //已经存在，直接移除
                    attrMapList.get(utxo.getAddress()).getHashIndexSet().remove(utxo.getKey());
                } else {
                    //不存在，新建一个，去leveldb获取数据，然后删除
                    addressHashIndex = new AddressHashIndex();
                    addressHashIndex.setAddress(utxo.getAddress());
                    Set<String> setList = UtxoContext.get(utxo.getAddress());
                    setList.remove(utxo.getKey());
                    addressHashIndex.setHashIndexSet(setList);
                    attrMapList.put(utxo.getAddress(), addressHashIndex);
                }
            }
            //新的未花费
            for (Utxo utxo : utxoMap.values()) {
                if (utxo.getSpendTxHash() == null) {
                    AddressHashIndex addressHashIndex = null;
                    if (attrMapList.containsKey(utxo.getAddress())) {
                        //已经存在，直接再新增一个
                        attrMapList.get(utxo.getAddress()).getHashIndexSet().add(utxo.getKey());
                    } else {
                        //不存在，新建一个，去leveldb获取数据
                        addressHashIndex = new AddressHashIndex();
                        addressHashIndex.setAddress(utxo.getAddress());
                        Set<String> setList = UtxoContext.get(utxo.getAddress());
                        setList.add(utxo.getKey());
                        addressHashIndex.setHashIndexSet(setList);
                        attrMapList.put(utxo.getAddress(), addressHashIndex);
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
        Log.info("高度：" + block.getHeader().getHeight() + "---交易笔数：" + txList.size() + "---保存耗时：" + (time2 - time1));
        utxoMap = null;
        fromList = null;
        txList = null;
        txRelationList = null;
        addressRewardDetailList = null;
        aliasList = null;
        depositList = null;
        punishLogList = null;
        contractAddressList = null;
        contractCreateDataList = null;
        deleteContractDataList = null;
        callContractDataList = null;
        contractResultInfoList = null;
        contractTokenInfoList = null;
        contractTransactionList = null;
        contractTokenTransferInfoList = null;
    }

    /**
     * 检查nuls主网是否已升级，未升级时检查每个区块头的升级信息
     * 一旦出现新版本的区块，就视为升级成功
     */
    private void checkNulsProtocolUpgrade(BlockHeader blockHeader) {
        //如果已经记录了升级高度，直接退出
        if (NulsContext.CHANGE_HASH_SERIALIZE_HEIGHT != null) {
            return;
        }

        BlockExtendsData extendsData = new BlockExtendsData(blockHeader.getTempExtend());
        if (extendsData.getCurrentVersion() != null && extendsData.getMainVersion() == 2) {
            NulsContext.MAIN_NET_VERSION = extendsData.getMainVersion();
            NulsContext.CHANGE_HASH_SERIALIZE_HEIGHT = blockHeader.getHeight();
            protocolLevelDbService.saveChangeTxHashBlockHeight(blockHeader.getHeight());
        }
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
            //回滚代币转账交易记录
            contractBusiness.rollbackContractTokenTransferInfo(header.getTxHashList());
            //回滚智能合约交易执行结果
            contractBusiness.deleteContractResultList(header.getTxHashList());
            //回滚智能合约token代币信息
            contractBusiness.deleteTokenList(header.getTxHashList());
            //回滚智能合约交易记录
            contractBusiness.deleteTransactionList(header.getTxHashList());
            //回滚智能合约创建交易数据
            contractBusiness.deleteCreateDataList(header.getTxHashList());
            //回滚删除合约交易记录
            contractBusiness.rollbackContractDeleteInfo(header.getTxHashList());
            //回滚调用合约交易记录
            contractBusiness.rollbackContractCallInfo(header.getTxHashList());
            //回滚智能合约地址
            contractBusiness.deleteContractByHeight(header.getHeight());
            //回滚合约内部转账交易记录
            contractBusiness.deleteAllTransferInfo(header.getTxHashList());

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

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
