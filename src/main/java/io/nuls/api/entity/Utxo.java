package io.nuls.api.entity;

public class Utxo {
    private Long id;

    private String txHash;

    private Integer txIndex;

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

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public Integer getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(Integer txIndex) {
        this.txIndex = txIndex;
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
    public boolean equals(Object obj) {
        Utxo utxo = (Utxo) obj;
        if (this.getTxHash().equals(utxo.getTxHash()) && this.getTxIndex() == utxo.getTxIndex()) {
            return true;
        }
        return false;
    }
}