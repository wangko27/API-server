package io.nuls.api.entity;

public class UtxoKey {
    private String txHash;

    private Long txIndex;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public Long getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(Long txIndex) {
        this.txIndex = txIndex;
    }
}