package io.nuls.api.entity;

import java.util.List;

public class BlockHeader {

    private String hash;

    private Long height;

    private String preHash;

    private String merkleHash;

    private Long createTime;

    private String consensusAddress;

    private Integer txCount;

    private Integer roundIndex;

    private Long totalFee;

    private Long reward;

    private Integer size;

    private Integer packingIndexOfRound;

    private Long roundStartTime;

    private byte[] extend;

    /**
     * 冗余字段，存储当前块所有交易的hash
     */
    private List<String> txList;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
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
        this.preHash = prevHash == null ? null : prevHash.trim();
    }

    public String getMerkleHash() {
        return merkleHash;
    }

    public void setMerkleHash(String merkleHash) {
        this.merkleHash = merkleHash == null ? null : merkleHash.trim();
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
        this.consensusAddress = consensusAddress == null ? null : consensusAddress.trim();
    }

    public Integer getTxCount() {
        return txCount;
    }

    public void setTxCount(Integer txCount) {
        this.txCount = txCount;
    }

    public Integer getRoundIndex() {
        return roundIndex;
    }

    public void setRoundIndex(Integer roundIndex) {
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

    public byte[] getExtend() {
        return extend;
    }

    public void setExtend(byte[] extend) {
        this.extend = extend;
    }

    public List<String> getTxList() {
        return txList;
    }

    public void setTxList(List<String> txList) {
        this.txList = txList;
    }
}