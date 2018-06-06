package io.nuls.api.utils;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.crypto.Hex;
import io.nuls.api.entity.*;
import io.nuls.api.entity.Alias;
import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Deposit;
import io.nuls.api.entity.Transaction;
import io.nuls.api.model.*;
import io.nuls.api.model.tx.*;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
        blockHeader.setRoundStartTime((Long) map.get("roundStartTime"));
        blockHeader.setCreateTime((Long) map.get("time"));
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
        byte[] data = Hex.decode(hexBlock);
        io.nuls.api.model.Block blockModel = new io.nuls.api.model.Block();
        blockModel.parse(data);

        Block block = new Block();
        header.setSize(blockModel.size());
        block.setHeader(header);

        List<Transaction> txList = new ArrayList<>();
        for (int i = 0; i < blockModel.getTxs().size(); i++) {
            io.nuls.api.model.Transaction txModel = blockModel.getTxs().get(i);
            Transaction tx = toTransaction(txModel, header);
            txList.add(tx);
        }
        block.setTxList(txList);
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
        }
        return tx;
    }

    private static Transaction transferTx(io.nuls.api.model.Transaction txModel) throws Exception {
        Transaction tx = new Transaction();
        tx.setHash(txModel.getHash().getDigestHex());
        tx.setBlockHeight(txModel.getBlockHeight());
        tx.setFee(txModel.getFee().getValue());
        if (tx.getRemark() != null) {
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
        if (txModel.getCoinData() != null && txModel.getCoinData().getFrom() != null) {
            for (Coin coin : txModel.getCoinData().getFrom()) {
                owner = coin.getOwner();
                hashByte = LedgerUtil.getTxHashBytes(owner);
                index = LedgerUtil.getIndex(owner);

                Input input = new Input();
                NulsDigestData hash = new NulsDigestData();
                hash.parse(hashByte);
                input.setFromHash(hash.getDigestHex());
                input.setFromIndex(index);
                inputs.add(input);
            }
        }
        tx.setInputs(inputs);

        List<Utxo> outputs = new ArrayList<>();
        List<Output> outputList = new ArrayList<>();
        if (txModel.getCoinData() != null && txModel.getCoinData().getTo() != null) {
            for (int i = 0; i < txModel.getCoinData().getTo().size(); i++) {
                Coin coin = txModel.getCoinData().getTo().get(i);
                Utxo utxo = new Utxo();
                utxo.setTxHash(tx.getHash());
                utxo.setTxIndex(i);
                utxo.setAmount(coin.getNa().getValue());
                utxo.setAddress(Base58.encode(coin.getOwner()));
                utxo.setLockTime(Long.parseLong(String.valueOf(coin.getLockTime())));
                outputs.add(utxo);

                Output output = new Output(utxo.getTxHash(), utxo.getAddress(), utxo.getAmount());
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
        alias.setAddress(Base58.encode(model.getAddress()));
        alias.setAlias(model.getAlias());
        alias.setBlockHeight(tx.getBlockHeight());
        return alias;
    }

    private static AgentNode toAgentNode(CreateAgentTransaction tx) throws UnsupportedEncodingException {
        Agent model = tx.getTxData();

        AgentNode agent = new AgentNode();
        agent.setTxHash(tx.getHash().getDigestHex());
        agent.setAgentAddress(Base58.encode(model.getAgentAddress()));
        agent.setPackingAddress(Base58.encode(model.getPackingAddress()));
        agent.setRewardAddress(Base58.encode(model.getRewardAddress()));
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
        deposit.setAddress(Base58.encode(model.getAddress()));
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
            log.setAddress(Base58.encode(address));
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
        punishLog.setAddress(Base58.encode(model.getAddress()));
        punishLog.setEvidence(model.getEvidence());
        punishLog.setBlockHeight(tx.getBlockHeight());
        punishLog.setTime(tx.getTime());
        punishLog.setRoundIndex(header.getRoundIndex());
        //        punishLog.setReason(new String (model.get);
        return punishLog;

    }


    public static Transaction toTransaction(Map<String, Object> map) throws Exception {
        Transaction tx = new Transaction();
        tx.setBlockHeight(Long.parseLong(map.get("blockHeight").toString()));
        tx.setHash((String) map.get("hash"));
        tx.setFee(Long.parseLong(map.get("fee").toString()));
        tx.setRemark(map.get("remark").toString());
        tx.setSize((Integer) map.get("size"));
        tx.setType((Integer) map.get("type"));
        tx.setCreateTime((Long) map.get("time"));

        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> inputMaps = (List<Map<String, Object>>) map.get("inputs");

        dataMap.put("inputs", inputMaps);
        dataMap.put("scriptSign", map.get("scriptSig").toString());
        tx.setExtend(JSONUtils.obj2json(dataMap).getBytes());

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
            output.setTxHash((String) dataMap.get("txHash"));
            output.setTxIndex((Integer) dataMap.get("index"));
            output.setAddress((String) dataMap.get("address"));
            output.setLockTime(Long.parseLong(dataMap.get("lockTime").toString()));
            output.setAmount(Long.parseLong(dataMap.get("value").toString()));
            outputs.add(output);
        }

        return tx;
    }
}
