package io.nuls.api.entity;

import java.util.Objects;

public class Utxo {
    private Long id;

    private String hashIndex;

    private String address;

    private Long amount;

    private Long lockTime;

    private String spendTxHash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHashIndex() {
        return hashIndex;
    }

    public void setHashIndex(String hashIndex) {
        this.hashIndex = hashIndex == null ? null : hashIndex.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
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
        this.spendTxHash = spendTxHash == null ? null : spendTxHash.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utxo)) return false;
        Utxo utxo = (Utxo) o;
        return Objects.equals(getId(), utxo.getId()) &&
                Objects.equals(getHashIndex(), utxo.getHashIndex()) &&
                Objects.equals(getSpendTxHash(), utxo.getSpendTxHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getHashIndex(), getSpendTxHash());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id").append(id).append("--");
        sb.append("hashIndex").append(hashIndex).append("--");
        sb.append("address").append(address).append("--");
        sb.append("amount").append(amount).append("--");
        sb.append("lockTime").append(lockTime).append("--");
        sb.append("spendTxHash").append(spendTxHash).append("--");
        return sb.toString();
    }

    public String toHashIndex(String hash,Integer index){
        return hash+"_"+index;
    }
}