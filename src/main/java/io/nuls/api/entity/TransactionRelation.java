package io.nuls.api.entity;

public class TransactionRelation {

    public TransactionRelation(){

    }
    public TransactionRelation(String address,String txHash){
        this.address = address;
        this.txHash = txHash;
    }
    private Long id;

    private String address;

    private String txHash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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