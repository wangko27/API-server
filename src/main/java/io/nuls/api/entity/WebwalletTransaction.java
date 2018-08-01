package io.nuls.api.entity;

import io.nuls.api.constant.EntityConstant;

import java.util.List;

public class WebwalletTransaction {

    public WebwalletTransaction(){

    }

    public WebwalletTransaction(Transaction tx,String signData,String address){
        this.hash = tx.getHash();
        this.type = tx.getType();
        this.status = EntityConstant.WEBWALLET_STATUS_NOTCONFIRM;
        this.time = tx.getCreateTime();
        this.inputs = tx.getInputs();
        this.outputs = tx.getOutputs();
        this.remark = tx.getRemark();
        this.signData = signData;
        this.address = address;
        this.outputList = tx.getOutputList();
    }

    private String hash;

    private Integer type;

    private Integer status;

    private Long time;

    private String remark;

    private String address;

    private List<Input> inputs;

    private List<Utxo> outputs;

    private List<Output> outputList;

    private String signData;

    private long amount = 0;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public List<Output> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<Output> outputList) {
        this.outputList = outputList;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    public List<Utxo> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Utxo> outputs) {
        this.outputs = outputs;
    }

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }
}