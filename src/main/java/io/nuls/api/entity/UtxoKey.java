package io.nuls.api.entity;

import java.io.Serializable;

public class UtxoKey implements Serializable {

    private String txHash;

    private Integer txIndex;

    public UtxoKey() {
    }

    public UtxoKey(String txHash, Integer txIndex) {
        this.txHash = txHash;
        this.txIndex = txIndex;
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
}