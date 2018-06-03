package io.nuls.api.entity;

import java.io.Serializable;

public class TransactionRelationKey implements Serializable {

    private String address;

    private String txHash;

    public TransactionRelationKey() {
    }

    public TransactionRelationKey(String address, String txHash) {
        this.address = address;
        this.txHash = txHash;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }
}