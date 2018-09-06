package io.nuls.api.server.dto;

/**
 * Description:
 * Author: moon
 * Date:  2018/8/2 0002
 */
public class FreezeDto {
    private Long createTime;
    private Long lockTime;
    private String txHash;
    private int txType;
    private long value;

    public FreezeDto(Long createTime, Long lockTime, String txHash, int txType, long value) {
        this.createTime = createTime;
        this.lockTime = lockTime;
        this.txHash = txHash;
        this.txType = txType;
        this.value = value;
    }
    public FreezeDto(){

    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLockTime() {
        return lockTime;
    }

    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public int getTxType() {
        return txType;
    }

    public void setTxType(int txType) {
        this.txType = txType;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
