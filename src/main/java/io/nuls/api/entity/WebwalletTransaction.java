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

    public void caclTx(WebwalletTransaction tx,String address){
        if(tx.getType() == EntityConstant.TX_TYPE_COINBASE){
            if(null != tx.getOutputList()){
                for(Output output:tx.getOutputList()){
                    if(address.equals(output.getAddress())){
                        tx.setAmount(tx.getAmount()+output.getValue());
                    }
                }
            }
        }else if(tx.getType() == EntityConstant.TX_TYPE_TRANSFER){
            if(null!=tx.getInputs()){
                for(Input input:tx.getInputs()){
                    if(address.equals(input.getAddress())){
                        tx.setAmount(tx.getAmount()-input.getValue());
                    }
                }
            }
            if(null != tx.getOutputList()){
                for(Output output:tx.getOutputList()){
                    if(address.equals(output.getAddress())){
                        tx.setAmount(tx.getAmount()+output.getValue());
                    }
                }
            }
        }else if(tx.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS){
            tx.setAmount(Na.NA.getValue());
        }else if(tx.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS || tx.getType() == EntityConstant.TX_TYPE_REGISTER_AGENT){
            for(Utxo utxo:tx.getOutputs()){
                if(address.equals(utxo.getAddress()) && utxo.getLockTime()==-1){
                    tx.setAmount(0-tx.getAmount());
                }
            }
        }else if(tx.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT){
            for(Utxo utxo:tx.getOutputs()){
                if(address.equals(utxo.getAddress()) && utxo.getLockTime()==0){
                    tx.setAmount(tx.getAmount());
                }
            }
        }else if(tx.getType() == EntityConstant.TX_TYPE_STOP_AGENT){
            for(Utxo utxo:tx.getOutputs()){
                if(address.equals(utxo.getAddress())){
                    tx.setAmount(tx.getAmount());
                }
            }
        }
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