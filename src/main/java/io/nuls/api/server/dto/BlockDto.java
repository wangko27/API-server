package io.nuls.api.server.dto;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.utils.JSONUtils;

import java.util.Map;

public class BlockDto {

    private String hash;

    private Long height;

    private String preHash;

    private String merkleHash;

    private Long createTime;

    private String consensusAddress;

    private Integer txCount;

    private Long roundIndex;

    private Long totalFee;

    private Long reward;

    private Integer size;

    private Integer packingIndexOfRound;

    private Long roundStartTime;

    private String scriptSign;

    private Long total;

    public BlockDto() {

    }

    public BlockDto(BlockHeader header) {
        this.hash = header.getHash();
        this.height = header.getHeight();
        this.preHash = header.getPreHash();
        this.merkleHash = header.getMerkleHash();
        this.createTime = header.getCreateTime();
        this.consensusAddress = header.getConsensusAddress();
        this.txCount = header.getTxCount();
        this.roundIndex = header.getRoundIndex();
        this.totalFee = header.getTotalFee();
        this.reward = header.getReward();
        this.size = header.getSize();
        this.packingIndexOfRound = header.getPackingIndexOfRound();
        this.roundStartTime = header.getRoundStartTime();

        String extend = new String(header.getExtend());
        try {
            Map<String, Object> map = JSONUtils.json2map(extend);
            String scriptSign = (String) map.get("scriptSign");
            this.scriptSign = scriptSign;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String prevHash) {
        this.preHash = prevHash;
    }

    public String getMerkleHash() {
        return merkleHash;
    }

    public void setMerkleHash(String merkleHash) {
        this.merkleHash = merkleHash;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getConsensusAddress() {
        return consensusAddress;
    }

    public void setConsensusAddress(String consensusAddress) {
        this.consensusAddress = consensusAddress;
    }

    public Integer getTxCount() {
        return txCount;
    }

    public void setTxCount(Integer txCount) {
        this.txCount = txCount;
    }

    public Long getRoundIndex() {
        return roundIndex;
    }

    public void setRoundIndex(Long roundIndex) {
        this.roundIndex = roundIndex;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public Long getReward() {
        return reward;
    }

    public void setReward(Long reward) {
        this.reward = reward;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPackingIndexOfRound() {
        return packingIndexOfRound;
    }

    public void setPackingIndexOfRound(Integer packingIndexOfRound) {
        this.packingIndexOfRound = packingIndexOfRound;
    }

    public Long getRoundStartTime() {
        return roundStartTime;
    }

    public void setRoundStartTime(Long roundStartTime) {
        this.roundStartTime = roundStartTime;
    }

    public String getScriptSign() {
        return scriptSign;
    }

    public void setScriptSign(String scriptSign) {
        this.scriptSign = scriptSign;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
