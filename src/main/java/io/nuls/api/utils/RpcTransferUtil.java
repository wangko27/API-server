package io.nuls.api.utils;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.crypto.Hex;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.Alias;
import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.entity.ContractCreateInfo;
import io.nuls.api.entity.ContractDeleteInfo;
import io.nuls.api.entity.ContractResultInfo;
import io.nuls.api.entity.Deposit;
import io.nuls.api.entity.Input;
import io.nuls.api.entity.Output;
import io.nuls.api.entity.PunishLog;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.TxData;
import io.nuls.api.entity.Utxo;
import io.nuls.api.model.Agent;
import io.nuls.api.model.CancelDeposit;
import io.nuls.api.model.Coin;
import io.nuls.api.model.CreateContractData;
import io.nuls.api.model.DeleteContractData;
import io.nuls.api.model.NulsDigestData;
import io.nuls.api.model.RedPunishData;
import io.nuls.api.model.StopAgent;
import io.nuls.api.model.YellowPunishData;
import io.nuls.api.model.tx.AliasTransaction;
import io.nuls.api.model.tx.CancelDepositTransaction;
import io.nuls.api.model.tx.CreateAgentTransaction;
import io.nuls.api.model.tx.CreateContractTransaction;
import io.nuls.api.model.tx.DeleteContractTransaction;
import io.nuls.api.model.tx.DepositTransaction;
import io.nuls.api.model.tx.RedPunishTransaction;
import io.nuls.api.model.tx.StopAgentTransaction;
import io.nuls.api.model.tx.YellowPunishTransaction;
import io.nuls.api.server.dto.contract.ProgramStatus;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcTransferUtil {

    public static BlockHeader toBlockHeader(Map<String, Object> map) throws Exception {
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setHash((String) map.get("hash"));
        blockHeader.setTxCount((Integer) map.get("txCount"));
        blockHeader.setSize((Integer) map.get("size"));
        blockHeader.setMerkleHash((String) map.get("merkleHash"));
        blockHeader.setPreHash((String) map.get("preHash"));
        blockHeader.setHeight(Long.parseLong(map.get("height").toString()));
        blockHeader.setPackingIndexOfRound((Integer) map.get("packingIndexOfRound"));
        blockHeader.setReward(Long.parseLong(map.get("reward").toString()));
        blockHeader.setRoundIndex(Long.parseLong(map.get("roundIndex").toString()));
        blockHeader.setTotalFee(Long.parseLong(map.get("fee").toString()));
        blockHeader.setRoundStartTime(Long.parseLong(map.get("roundStartTime").toString()));
        blockHeader.setCreateTime(Long.parseLong(map.get("time").toString()));
        blockHeader.setConsensusAddress((String) map.get("packingAddress"));

        String scriptSign = (String) map.get("scriptSig");
        String extend = (String) map.get("extend");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("scriptSign", scriptSign);
        dataMap.put("extend", extend);
        blockHeader.setExtend(JSONUtils.obj2json(dataMap).getBytes());
        return blockHeader;
    }

    public static Block toBlock(String hexBlock, BlockHeader header) throws Exception {
        byte[] data = Base64.getDecoder().decode(hexBlock);
        io.nuls.api.model.Block blockModel = new io.nuls.api.model.Block();
        blockModel.parse(new NulsByteBuffer(data));

        Block block = new Block();
        header.setSize(blockModel.size());
        block.setHeader(header);

        List<Transaction> txList = new ArrayList<>();
        List<String> txHashList = new ArrayList<>();
        for (int i = 0; i < blockModel.getTxs().size(); i++) {
            io.nuls.api.model.Transaction txModel = blockModel.getTxs().get(i);
            Transaction tx = toTransaction(txModel, header);
            txList.add(tx);
            txHashList.add(tx.getHash());
        }

        block.setTxList(txList);
        block.getHeader().setTxHashList(txHashList);
        return block;
    }

    public static Transaction toTransaction(io.nuls.api.model.Transaction txModel, BlockHeader header) throws Exception {
        Transaction tx = transferTx(txModel);
        if (txModel.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS) {
            AliasTransaction aliasTx = (AliasTransaction) txModel;
            tx.setTxData(toAlias(aliasTx));
        } else if (txModel.getType() == EntityConstant.TX_TYPE_REGISTER_AGENT) {
            CreateAgentTransaction createAgentTx = (CreateAgentTransaction) txModel;
            AgentNode agentNode = toAgentNode(createAgentTx);
            tx.setTxData(agentNode);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS) {
            DepositTransaction depositTx = (DepositTransaction) txModel;
            Deposit deposit = toDeposit(depositTx);
            tx.setTxData(deposit);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT) {
            CancelDepositTransaction cancelDepositTx = (CancelDepositTransaction) txModel;
            Deposit deposit = toCancelDeposit(cancelDepositTx);
            tx.setTxData(deposit);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
            StopAgentTransaction stopAgentTx = (StopAgentTransaction) txModel;
            AgentNode agentNode = toStopAgent(stopAgentTx);
            tx.setTxData(agentNode);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_YELLOW_PUNISH) {
            YellowPunishTransaction yellowPunishTx = (YellowPunishTransaction) txModel;
            List<TxData> punishLogList = toYellowPunishLog(yellowPunishTx, header);
            tx.setTxDataList(punishLogList);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_RED_PUNISH) {
            RedPunishTransaction redPunishTx = (RedPunishTransaction) txModel;
            PunishLog log = toRedPublishLog(redPunishTx, header);
            tx.setTxData(log);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_CREATE_CONTRACT) {
            CreateContractTransaction createContractTx = (CreateContractTransaction) txModel;
            ContractCreateInfo createData = toContractCreateData(createContractTx);
            tx.setTxData(createData);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_CALL_CONTRACT) {

        } else if (txModel.getType() == EntityConstant.TX_TYPE_DELETE_CONTRACT) {
            DeleteContractTransaction deleteContractTx = (DeleteContractTransaction) txModel;
            ContractDeleteInfo data = toContractDeleteInfo(deleteContractTx);
            tx.setTxData(data);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_CONTRACT_TRANSFER) {

        }
        return tx;
    }

    public static Transaction toTransaction(io.nuls.api.model.Transaction txModel) throws Exception {
        Transaction tx = transferTx(txModel);
        if (txModel.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS) {
            AliasTransaction aliasTx = (AliasTransaction) txModel;
            tx.setTxData(toAlias(aliasTx));
        } else if (txModel.getType() == EntityConstant.TX_TYPE_REGISTER_AGENT) {
            CreateAgentTransaction createAgentTx = (CreateAgentTransaction) txModel;
            AgentNode agentNode = toAgentNode(createAgentTx);
            tx.setTxData(agentNode);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS) {
            DepositTransaction depositTx = (DepositTransaction) txModel;
            Deposit deposit = toDeposit(depositTx);
            tx.setTxData(deposit);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT) {
            CancelDepositTransaction cancelDepositTx = (CancelDepositTransaction) txModel;
            Deposit deposit = toCancelDeposit(cancelDepositTx);
            tx.setTxData(deposit);
        } else if (txModel.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
            StopAgentTransaction stopAgentTx = (StopAgentTransaction) txModel;
            AgentNode agentNode = toStopAgent(stopAgentTx);
            tx.setTxData(agentNode);
        }
        return tx;
    }

    private static Transaction transferTx(io.nuls.api.model.Transaction txModel) throws Exception {
        Transaction tx = new Transaction();
        tx.setHash(txModel.getHash().getDigestHex());
        tx.setBlockHeight(txModel.getBlockHeight());
        tx.setFee(txModel.getFee().getValue());
        if (txModel.getRemark() != null) {
            try {
                tx.setRemark(new String(txModel.getRemark(), Constant.DEFAULT_ENCODING));
            } catch (UnsupportedEncodingException e) {
                tx.setRemark(Hex.encode(txModel.getRemark()));
            }
        }
        tx.setSize(txModel.getSize());
        tx.setType(txModel.getType());
        tx.setCreateTime(txModel.getTime());
        if (txModel.getScriptSig() != null) {
            tx.setScriptSign(Hex.encode(txModel.getScriptSig()));
        }

        List<Input> inputs = new ArrayList<>();
        byte[] hashByte, owner;
        int index;
        Input input = null;
        if (txModel.getCoinData() != null && txModel.getCoinData().getFrom() != null) {
            for (Coin coin : txModel.getCoinData().getFrom()) {
                owner = coin.getOwner();
                hashByte = LedgerUtil.getTxHashBytes(owner);
                index = LedgerUtil.getIndex(owner);

                input = new Input();
                NulsDigestData hash = new NulsDigestData();
                hash.parse(new NulsByteBuffer(hashByte));
                input.setFromHash(hash.getDigestHex());
                input.setFromIndex(index);
                inputs.add(input);
            }
        }
        tx.setInputs(inputs);

        List<Utxo> outputs = new ArrayList<>();
        List<Output> outputList = new ArrayList<>();
        Utxo utxo = null;
        Coin coin = null;
        Output output = null;
        if (txModel.getCoinData() != null && txModel.getCoinData().getTo() != null) {
            for (int i = 0; i < txModel.getCoinData().getTo().size(); i++) {
                coin = txModel.getCoinData().getTo().get(i);
                utxo = new Utxo();
                utxo.setTxHash(tx.getHash());
                utxo.setTxIndex(i);
                utxo.setAmount(coin.getNa().getValue());
                utxo.setAddress(AddressTool.getStringAddressByBytes(coin.getOwner()));
                utxo.setLockTime(coin.getLockTime());
                outputs.add(utxo);

                output = new Output(tx.getHash(), i, utxo.getAddress(), utxo.getAmount());
                outputList.add(output);
            }
        }
        tx.setOutputs(outputs);
        tx.setOutputList(outputList);
        return tx;
    }

    private static Alias toAlias(AliasTransaction tx) {
        io.nuls.api.model.Alias model = tx.getTxData();
        Alias alias = new Alias();
        alias.setAddress(AddressTool.getStringAddressByBytes(model.getAddress()));
        alias.setAlias(model.getAlias());
        alias.setBlockHeight(tx.getBlockHeight());
        return alias;
    }

    private static AgentNode toAgentNode(CreateAgentTransaction tx) throws UnsupportedEncodingException {
        Agent model = tx.getTxData();

        AgentNode agent = new AgentNode();
        agent.setTxHash(tx.getHash().getDigestHex());
        agent.setAgentAddress(AddressTool.getStringAddressByBytes(model.getAgentAddress()));
        agent.setPackingAddress(AddressTool.getStringAddressByBytes(model.getPackingAddress()));
        agent.setRewardAddress(AddressTool.getStringAddressByBytes(model.getRewardAddress()));
        agent.setDeposit(model.getDeposit().getValue());
        agent.setCommissionRate(new BigDecimal(model.getCommissionRate()));
        agent.setBlockHeight(tx.getBlockHeight());
        agent.setStatus(model.getStatus());
        agent.setDepositCount(model.getMemberCount());
        agent.setCreditValue(new BigDecimal(model.getCreditVal()));
        agent.setCreateTime(tx.getTime());
        agent.setTxHash(tx.getHash().getDigestHex());
        return agent;
    }

    private static Deposit toDeposit(DepositTransaction tx) {
        io.nuls.api.model.Deposit model = tx.getTxData();

        Deposit deposit = new Deposit();
        deposit.setTxHash(tx.getHash().getDigestHex());
        deposit.setAmount(model.getDeposit().getValue());
        deposit.setAgentHash(model.getAgentHash().getDigestHex());
        deposit.setAddress(AddressTool.getStringAddressByBytes(model.getAddress()));
        deposit.setTxHash(tx.getHash().getDigestHex());
        deposit.setBlockHeight(tx.getBlockHeight());
        deposit.setCreateTime(tx.getTime());
        return deposit;
    }

    private static Deposit toCancelDeposit(CancelDepositTransaction tx) {
        CancelDeposit cancelDeposit = tx.getTxData();
        Deposit deposit = new Deposit();
        deposit.setTxHash(cancelDeposit.getJoinTxHash().getDigestHex());
        return deposit;
    }

    private static AgentNode toStopAgent(StopAgentTransaction tx) {
        StopAgent stopAgent = tx.getTxData();
        AgentNode agentNode = new AgentNode();
        agentNode.setTxHash(stopAgent.getCreateTxHash().getDigestHex());
        return agentNode;
    }

    private static List<TxData> toYellowPunishLog(YellowPunishTransaction tx, BlockHeader header) {
        YellowPunishData model = tx.getTxData();

        List<TxData> logList = new ArrayList<>();
        for (byte[] address : model.getAddressList()) {
            PunishLog log = new PunishLog();
            log.setAddress(AddressTool.getStringAddressByBytes(address));
            log.setBlockHeight(tx.getBlockHeight());
            log.setTime(tx.getTime());
            log.setType(EntityConstant.PUBLISH_YELLOW);
            log.setRoundIndex(header.getRoundIndex());
            log.setReason("The packing block is too late");
            logList.add(log);
        }
        return logList;
    }

    private static PunishLog toRedPublishLog(RedPunishTransaction tx, BlockHeader header) {
        RedPunishData model = tx.getTxData();

        PunishLog punishLog = new PunishLog();
        punishLog.setType(EntityConstant.PUTLISH_RED);
        punishLog.setAddress(AddressTool.getStringAddressByBytes(model.getAddress()));
        punishLog.setEvidence(model.getEvidence());
        punishLog.setBlockHeight(tx.getBlockHeight());
        punishLog.setTime(tx.getTime());
        punishLog.setRoundIndex(header.getRoundIndex());
        //        punishLog.setReason(new String (model.get);
        return punishLog;

    }

    private static ContractCreateInfo toContractCreateData(CreateContractTransaction tx) {
        CreateContractData model = tx.getTxData();
        if (model != null) {
            ContractCreateInfo contractAddress = new ContractCreateInfo(model);
            return contractAddress;
        }
        return new ContractCreateInfo();
    }


    @Deprecated
    public static Transaction toTransaction(Map<String, Object> map) throws Exception {
        Transaction tx = new Transaction();
        tx.setBlockHeight(Long.parseLong(map.get("blockHeight").toString()));
        tx.setHash((String) map.get("hash"));
        tx.setFee(Long.parseLong(map.get("fee").toString()));
        tx.setRemark(map.get("remark").toString());
        tx.setSize((Integer) map.get("size"));
        tx.setType((Integer) map.get("type"));
        tx.setCreateTime(Long.parseLong(map.get("time").toString()));

        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> inputMaps = (List<Map<String, Object>>) map.get("inputs");

        dataMap.put("inputs", inputMaps);
        dataMap.put("scriptSign", map.get("scriptSig").toString());
        //   tx.setExtend(JSONUtils.obj2json(dataMap).getBytes());

        List<Input> inputs = new ArrayList<>();
        for (int i = 0; i < inputMaps.size(); i++) {
            Input input = new Input();
            dataMap = inputMaps.get(i);
            input.setAddress((String) dataMap.get("address"));
            input.setFromHash((String) dataMap.get("fromHash"));
            input.setValue(Long.parseLong(dataMap.get("value").toString()));
            input.setFromIndex((Integer) dataMap.get("fromIndex"));
            inputs.add(input);
        }
        tx.setInputs(inputs);

        List<Map<String, Object>> outputMaps = (List<Map<String, Object>>) map.get("outputs");
        List<Utxo> outputs = new ArrayList<>();
        for (int i = 0; i < outputMaps.size(); i++) {
            Utxo output = new Utxo();
            dataMap = outputMaps.get(i);
            output.setAddress((String) dataMap.get("address"));
            output.setLockTime(Long.parseLong(dataMap.get("lockTime").toString()));
            output.setAmount(Long.parseLong(dataMap.get("value").toString()));
            outputs.add(output);
        }
        return tx;
    }

    private static ContractDeleteInfo toContractDeleteInfo(DeleteContractTransaction tx) {
        DeleteContractData model = tx.getTxData();
        ContractDeleteInfo data = new ContractDeleteInfo(model);
        return data;

    }

    public static ContractAddressInfo toContract(Map<String, Object> map) throws Exception {
        ContractAddressInfo contractAddressInfo = new ContractAddressInfo();
        try {
            contractAddressInfo.setCreateTxHash((String) map.get("createTxHash"));
            contractAddressInfo.setContractAddress((String) map.get("address"));
            contractAddressInfo.setCreater((String) map.get("creater"));
            contractAddressInfo.setCreateTime(Long.parseLong(map.get("createTime").toString()));
            contractAddressInfo.setBlockHeight(Long.parseLong(map.get("blockHeight").toString()));
            contractAddressInfo.setIsNrc20(Boolean.parseBoolean(map.get("isNrc20").toString()) ? 1 : 0);
            //如果是NRC20需要解析代币信息
            if (Boolean.parseBoolean(map.get("isNrc20").toString())) {
                contractAddressInfo.setTokenName((String) map.get("nrc20TokenName"));
                contractAddressInfo.setSymbol((String) map.get("nrc20TokenSymbol"));
                contractAddressInfo.setDecimals(Long.parseLong(map.get("decimals").toString()));
                contractAddressInfo.setTotalsupply(map.get("totalSupply").toString());
            }
            contractAddressInfo.setStatus(ProgramStatus.codeOf((String) map.get("status")).getCode());
            contractAddressInfo.setMethods(JSONUtils.obj2json(map.get("method")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return contractAddressInfo;
    }

    public static ContractResultInfo toContractResult(Map<String, Object> map) throws Exception {

        ContractResultInfo result = null;
        try {
            result = new ContractResultInfo();
            map = (Map<String, Object>) map.get("data");
            result.setErrorMessage((String) map.get("errorMessage"));
            result.setSuccess(map.get("success").toString());
            result.setActualContractFee(Long.parseLong(map.get("actualContractFee").toString()));
            result.setBalance(map.get("balance") != null ? Long.parseLong(map.get("balance").toString()) : 0);
            result.setContractAddress((String) map.get("contractAddress"));
            result.setDecimals(map.get("decimals") != null ? Long.parseLong(map.get("decimals").toString()) : 0);
            result.setGasLimit(map.get("gasLimit") != null ? Long.parseLong(map.get("gasLimit").toString()) : 0);
            result.setGasUsed(map.get("gasUsed") != null ? Long.parseLong(map.get("gasUsed").toString()) : 0);
            result.setTokenName((String) map.get("name"));
            result.setNonce(Long.parseLong(map.get("nonce").toString()));
            result.setPrice(Long.parseLong(map.get("price").toString()));
            result.setRefundFee(map.get("refundFee") != null ? Long.parseLong(map.get("refundFee").toString()) : 0);
            result.setRemark((String) map.get("remark"));
            result.setResult((String) map.get("result"));
            result.setStacktrace((String) map.get("stackTrace"));
            result.setStateroot((String) map.get("stateRoot"));
            result.setSymbol((String) map.get("symbol"));
            result.setTotalFee(map.get("totalFee") != null ? Long.parseLong(map.get("totalFee").toString()) : 0);
            result.setTxSizeFee(map.get("txSizeFee") != null ? Long.parseLong(map.get("txSizeFee").toString()) : 0);
            result.setTxValue(map.get("value") != null ? Long.parseLong(map.get("value").toString()) : 0);
            String events = "";
            String transfers = "";
            String tokenTransfers = "";
            ArrayList list_events = (ArrayList) map.get("events");
            if (list_events.size() > 0) {
                events = JSONUtils.obj2json(list_events);
            }
            ArrayList list_transfers = (ArrayList) map.get("transfers");
            if (list_transfers.size() > 0) {
                transfers = JSONUtils.obj2json(list_transfers);
            }
            ArrayList list_tokenTransfers = (ArrayList) map.get("tokenTransfers");
            if (list_tokenTransfers.size() > 0) {
                tokenTransfers = JSONUtils.obj2json(list_tokenTransfers);
            }
            result.setEvents(events);
            result.setTransfers(transfers);
            result.setTokenTransfers(tokenTransfers);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
