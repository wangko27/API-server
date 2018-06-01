package io.nuls.api.utils;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.TransactionConstant;
import io.nuls.api.crypto.Hex;
import io.nuls.api.entity.*;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.Address;
import io.nuls.api.model.Agent;
import io.nuls.api.model.Coin;
import io.nuls.api.model.NulsDigestData;
import io.nuls.api.model.tx.AliasTransaction;
import io.nuls.api.model.tx.CoinBaseTransaction;
import io.nuls.api.model.tx.CreateAgentTransaction;

import java.io.UnsupportedEncodingException;
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
        blockHeader.setRoundIndex((Integer) map.get("roundIndex"));
        blockHeader.setTotalFee(Long.parseLong(map.get("fee").toString()));
        blockHeader.setRoundStartTime((Long) map.get("roundStartTime"));
        blockHeader.setCreateTime((Long) map.get("time"));
        blockHeader.setConsensusAddress((String) map.get("packingAddress"));

        String scriptSign = (String) map.get("scriptSig");
        String extend = (String) map.get("extend");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("scriptSign", scriptSign);
        dataMap.put("data", extend);
        blockHeader.setExtend(JSONUtils.obj2json(dataMap).getBytes());
        return blockHeader;
    }

    public static Block toBlock(String hexBlock) throws Exception {
        byte[] data = Hex.decode(hexBlock);
        io.nuls.api.model.Block blockModel = new io.nuls.api.model.Block();
        blockModel.parse(data);

        Block block = new Block();
        BlockHeader header = new BlockHeader();
        header.setHash(blockModel.getHeader().getHash().getDigestHex());
        header.setSize(blockModel.size());

        for (int i = 0; i < blockModel.getTxs().size(); i++) {
            io.nuls.api.model.Transaction txModel = blockModel.getTxs().get(i);


        }

        return block;
    }

    public static Transaction toTransaction(io.nuls.api.model.Transaction txModel) throws Exception {
        Transaction tx = transferTx(txModel);
        if (txModel.getType() == TransactionConstant.TX_TYPE_ACCOUNT_ALIAS) {
            AliasTransaction aliasTransaction = (AliasTransaction) txModel;
            tx.setTxData(toAlias(aliasTransaction));
        } else if (txModel.getType() == TransactionConstant.TX_TYPE_REGISTER_AGENT) {
            CreateAgentTransaction createAgentTransaction = (CreateAgentTransaction) txModel;
            Agent agent = createAgentTransaction.getTxData();

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
        tx.setScriptSign(Hex.encode(txModel.getScriptSig()));
        List<Input> inputs = new ArrayList<>();
        byte[] hashByte;
        int index;
        if (txModel.getCoinData().getFrom() != null) {
            for (Coin coin : txModel.getCoinData().getFrom()) {
                hashByte = LedgerUtil.getTxHashBytes(coin.getOwner());
                index = LedgerUtil.getIndex(coin.getOwner());

                Input input = new Input();
                NulsDigestData hash = new NulsDigestData();
                hash.parse(hashByte);
                input.setFromHash(hash.getDigestHex());
                input.setFromIndex(index);
                inputs.add(input);
            }
        }

        List<Utxo> outputs = new ArrayList<>();
        if (txModel.getCoinData().getTo() != null) {
            for (Coin coin : txModel.getCoinData().getTo()) {
                hashByte = LedgerUtil.getTxHashBytes(coin.getOwner());
                index = LedgerUtil.getIndex(coin.getOwner());

                Utxo utxo = new Utxo();
                NulsDigestData hash = new NulsDigestData();
                hash.parse(hashByte);
                utxo.setTxHash(hash.getDigestHex());
                utxo.setTxIndex(index);
                utxo.setAmount(coin.getNa().getValue());
                utxo.setAddress(AddressTool.getAddressBase58(coin.getOwner()));
                outputs.add(utxo);
            }
        }
        tx.setOutputs(outputs);
        return tx;
    }

    private static Alias toAlias(AliasTransaction tx) {
        io.nuls.api.model.Alias aliasModel = tx.getTxData();
        Alias alias = new Alias();
        alias.setAddress(AddressTool.getAddressBase58(aliasModel.getAddress()));
        alias.setAlias(aliasModel.getAlias());
        alias.setBlockHeight(tx.getBlockHeight());
        return alias;
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
