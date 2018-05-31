package io.nuls.api.utils;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Input;
import io.nuls.api.entity.Transaction;

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

        List<String> txList = new ArrayList<>();
        List<Map<String, Object>> txInfoList = (List<Map<String, Object>>) map.get("txList");
        Map<String, Object> txMap;
        if (txInfoList != null && !txInfoList.isEmpty()) {
            for (int i = 0; i < txInfoList.size(); i++) {
                txMap = txInfoList.get(i);
                txList.add((String) txMap.get("hash"));
            }
        }
        blockHeader.setTxList(txList);
        return blockHeader;
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
        List<Map<String, Object>> outputMaps = (List<Map<String, Object>>) map.get("outputs");
        dataMap.put("inputs", inputMaps);
        dataMap.put("outputs", outputMaps);
        dataMap.put("scriptSign", map.get("scriptSig").toString());
        tx.setExtend(JSONUtils.obj2json(dataMap).getBytes());

        List<Input> inputs = new ArrayList<>();
        for (int i = 0; i < inputMaps.size(); i++) {
            Input input = new Input();
            dataMap = inputMaps.get(i);
            input.setAddress((String) dataMap.get("address"));
            input.setFromHash((String) dataMap.get("fromHash"));
            input.setValue(Long.parseLong((String) dataMap.get("value")));
            input.setFromIndex((Integer) dataMap.get("fromIndex"));

        }
        return tx;
    }
}
