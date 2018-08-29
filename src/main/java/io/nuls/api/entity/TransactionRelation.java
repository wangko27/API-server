package io.nuls.api.entity;

public class TransactionRelation {

    public TransactionRelation(){

    }
    public TransactionRelation(String address,String txHash){
        this.address = address;
        this.txHash = txHash;
    }
    public TransactionRelation(String address,String txHash,int type,long createTime){
        this.address = address;
        this.txHash = txHash;
        this.type = type;
        this.createTime = createTime;
    }
    private Long id;

    private String address;

    private String txHash;

    private Integer type;

    private Long createTime;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}