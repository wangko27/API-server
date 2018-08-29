package io.nuls.api.entity;

import io.nuls.api.constant.EntityConstant;

import java.util.List;

public class WebwalletTransaction {

    public WebwalletTransaction(){

    }

    public WebwalletTransaction(Transaction tx,String signData,String address,String temp){
        this.hash = tx.getHash();
        this.type = tx.getType();
        this.status = EntityConstant.WEBWALLET_STATUS_NOTCONFIRM;
        this.createTime = tx.getCreateTime();
        this.inputs = tx.getInputs();
        this.outputs = tx.getOutputs();
        this.remark = tx.getRemark();
        this.signData = signData;
        this.address = address;
        this.outputList = tx.getOutputList();
        this.fee = tx.getFee();
        this.temp = temp;
    }

    public void caclTx(WebwalletTransaction tx,String address){
        //未确认的交易，我自己发起的，只有转账、设置别名、委托、退出委托四种
        tx.setAmount(0L);
        if(tx.getType() == EntityConstant.TX_TYPE_TRANSFER){
            if(null != tx.getOutputList()){
                for(Output output:tx.getOutputList()){
                    if(!address.equals(output.getAddress())){
                        tx.setAmount(tx.getAmount()-output.getValue());
                    }
                }
                tx.setAmount(tx.getAmount()-tx.getFee());
            }
        }else if(tx.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS){
            tx.setAmount(-Na.NA.getValue());
        }else if(tx.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS){
            for(Utxo utxo:tx.getOutputs()){
                if(address.equals(utxo.getAddress()) && utxo.getLockTime() == -1){
                    tx.setAmount(utxo.getAmount());
                }
            }
        }else if(tx.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT){
            for(Utxo utxo:tx.getOutputs()){
                if(address.equals(utxo.getAddress())){
                    tx.setAmount(utxo.getAmount()+tx.getFee());
                }
            }
        }
    }

    private String hash;

    private Integer type;

    private Integer status;

    private Long createTime;

    private String remark;

    private String address;

    private List<Input> inputs;

    private List<Utxo> outputs;

    private List<Output> outputList;

    private String signData;

    private String temp;//自定义值

    private long fee;

    private long amount = 0;

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}