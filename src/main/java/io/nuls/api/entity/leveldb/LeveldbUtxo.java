package io.nuls.api.entity.leveldb;

/**
 * Description:
 * Author: moon
 * Date:  2018/7/7 0007
 */
public class LeveldbUtxo {

    private String hashIndex;

    private String address;

    private Long amount;

    private Long lockTime;

    private String spendTxHash;

    public String getHashIndex() {
        return hashIndex;
    }

    public void setHashIndex(String hashIndex) {
        this.hashIndex = hashIndex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getLockTime() {
        return lockTime;
    }

    public void setLockTime(Long lockTime) {
        this.lockTime = lockTime;
    }

    public String getSpendTxHash() {
        return spendTxHash;
    }

    public void setSpendTxHash(String spendTxHash) {
        this.spendTxHash = spendTxHash;
    }

    public String toHashIndex(String hash, Integer index){
        return hash+"_"+index;
    }
}
