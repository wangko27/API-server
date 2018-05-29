package io.nuls.api.utils;

import io.nuls.api.entity.BlockHeader;
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
        Map<String, String> dataMap = new HashMap<>();
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
        Transaction transaction = new Transaction();
        return transaction;
    }
}
