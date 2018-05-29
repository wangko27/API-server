package io.nuls.api.utils;

import io.nuls.api.entity.BlockHeader;

import java.util.Map;

public class RpcTransferUtil {

    public static BlockHeader toBlockHeader(Map<String, Object> map) {
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setHash((String) map.get("hash"));
        blockHeader.setTxCount((Integer) map.get("txCount"));
        blockHeader.setSize((Integer) map.get("size"));
        blockHeader.setMerkleHash((String) map.get("merkleHash"));
        blockHeader.setPackingIndexOfRound((Integer) map.get("packingIndexOfRound"));
        blockHeader.setReward((Long) map.get("reward"));
        blockHeader.setRoundIndex((Integer) map.get("roundIndex"));
        blockHeader.setTotalFee((Long) map.get("fee"));
        blockHeader.setRoundStartTime((Long) map.get("roundStartTime"));
        blockHeader.setCreateTime((Long) map.get("time"));

        return blockHeader;
    }
}
