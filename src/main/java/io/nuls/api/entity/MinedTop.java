package io.nuls.api.entity;

import java.io.Serializable;

public class MinedTop implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mined_top.id
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mined_top.consensus_address
     *
     * @mbggenerated
     */
    private String consensusAddress;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mined_top.mined_count
     *
     * @mbggenerated
     */
    private Integer minedCount;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mined_top.reward
     *
     * @mbggenerated
     */
    private Long reward;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mined_top.last_height
     *
     * @mbggenerated
     */
    private Long lastHeight;

    private String consensusStatus;

    public String getConsensusStatus() {
        return consensusStatus;
    }

    public void setConsensusStatus(String consensusStatus) {
        this.consensusStatus = consensusStatus;
    }

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column mined_top.create_time
     *
     * @mbggenerated
     */
    private Long createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table mined_top
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mined_top.id
     *
     * @return the value of mined_top.id
     *
     * @mbggenerated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mined_top.id
     *
     * @param id the value for mined_top.id
     *
     * @mbggenerated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mined_top.consensus_address
     *
     * @return the value of mined_top.consensus_address
     *
     * @mbggenerated
     */
    public String getConsensusAddress() {
        return consensusAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mined_top.consensus_address
     *
     * @param consensusAddress the value for mined_top.consensus_address
     *
     * @mbggenerated
     */
    public void setConsensusAddress(String consensusAddress) {
        this.consensusAddress = consensusAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mined_top.mined_count
     *
     * @return the value of mined_top.mined_count
     *
     * @mbggenerated
     */
    public Integer getMinedCount() {
        return minedCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mined_top.mined_count
     *
     * @param minedCount the value for mined_top.mined_count
     *
     * @mbggenerated
     */
    public void setMinedCount(Integer minedCount) {
        this.minedCount = minedCount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mined_top.reward
     *
     * @return the value of mined_top.reward
     *
     * @mbggenerated
     */
    public Long getReward() {
        return reward;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mined_top.reward
     *
     * @param reward the value for mined_top.reward
     *
     * @mbggenerated
     */
    public void setReward(Long reward) {
        this.reward = reward;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mined_top.last_height
     *
     * @return the value of mined_top.last_height
     *
     * @mbggenerated
     */
    public Long getLastHeight() {
        return lastHeight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mined_top.last_height
     *
     * @param lastHeight the value for mined_top.last_height
     *
     * @mbggenerated
     */
    public void setLastHeight(Long lastHeight) {
        this.lastHeight = lastHeight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column mined_top.create_time
     *
     * @return the value of mined_top.create_time
     *
     * @mbggenerated
     */
    public Long getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column mined_top.create_time
     *
     * @param createTime the value for mined_top.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mined_top
     *
     * @mbggenerated
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        MinedTop other = (MinedTop) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getConsensusAddress() == null ? other.getConsensusAddress() == null : this.getConsensusAddress().equals(other.getConsensusAddress()))
            && (this.getMinedCount() == null ? other.getMinedCount() == null : this.getMinedCount().equals(other.getMinedCount()))
            && (this.getReward() == null ? other.getReward() == null : this.getReward().equals(other.getReward()))
            && (this.getLastHeight() == null ? other.getLastHeight() == null : this.getLastHeight().equals(other.getLastHeight()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mined_top
     *
     * @mbggenerated
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getConsensusAddress() == null) ? 0 : getConsensusAddress().hashCode());
        result = prime * result + ((getMinedCount() == null) ? 0 : getMinedCount().hashCode());
        result = prime * result + ((getReward() == null) ? 0 : getReward().hashCode());
        result = prime * result + ((getLastHeight() == null) ? 0 : getLastHeight().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mined_top
     *
     * @mbggenerated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", consensusAddress=").append(consensusAddress);
        sb.append(", minedCount=").append(minedCount);
        sb.append(", reward=").append(reward);
        sb.append(", lastHeight=").append(lastHeight);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}