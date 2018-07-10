package io.nuls.api.server.dto;

import io.nuls.api.entity.BlockHeader;

/**
 * Description:
 * Author: zsj
 * Date:  2018/6/7 0007
 */
public class BlockHeaderDto extends BlockHeader {
    private Long confirmCount = 0L;

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


    public BlockHeaderDto(){

    }

    public BlockHeaderDto(BlockHeader blockHeader){
        this.consensusAddress = blockHeader.getConsensusAddress();
        this.createTime = blockHeader.getCreateTime();
        this.hash = blockHeader.getHash();
        this.height = blockHeader.getHeight();
        this.merkleHash = blockHeader.getMerkleHash();
        this.packingIndexOfRound = blockHeader.getPackingIndexOfRound();
        this.preHash = blockHeader.getPreHash();
        this.reward = blockHeader.getReward();
        this.roundIndex = blockHeader.getRoundIndex();
        this.roundStartTime = blockHeader.getRoundStartTime();
        this.size = blockHeader.getSize();
        this.totalFee = blockHeader.getTotalFee();
        this.txCount = blockHeader.getTxCount();
    }

    public Long getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(Long confirmCount) {
        this.confirmCount = confirmCount;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public Long getHeight() {
        return height;
    }

    @Override
    public void setHeight(Long height) {
        this.height = height;
    }

    @Override
    public String getPreHash() {
        return preHash;
    }

    @Override
    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    @Override
    public String getMerkleHash() {
        return merkleHash;
    }

    @Override
    public void setMerkleHash(String merkleHash) {
        this.merkleHash = merkleHash;
    }

    @Override
    public Long getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getConsensusAddress() {
        return consensusAddress;
    }

    @Override
    public void setConsensusAddress(String consensusAddress) {
        this.consensusAddress = consensusAddress;
    }

    @Override
    public Integer getTxCount() {
        return txCount;
    }

    @Override
    public void setTxCount(Integer txCount) {
        this.txCount = txCount;
    }

    @Override
    public Long getRoundIndex() {
        return roundIndex;
    }

    @Override
    public void setRoundIndex(Long roundIndex) {
        this.roundIndex = roundIndex;
    }

    @Override
    public Long getTotalFee() {
        return totalFee;
    }

    @Override
    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    @Override
    public Long getReward() {
        return reward;
    }

    @Override
    public void setReward(Long reward) {
        this.reward = reward;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public Integer getPackingIndexOfRound() {
        return packingIndexOfRound;
    }

    @Override
    public void setPackingIndexOfRound(Integer packingIndexOfRound) {
        this.packingIndexOfRound = packingIndexOfRound;
    }

    @Override
    public Long getRoundStartTime() {
        return roundStartTime;
    }

    @Override
    public void setRoundStartTime(Long roundStartTime) {
        this.roundStartTime = roundStartTime;
    }
}
