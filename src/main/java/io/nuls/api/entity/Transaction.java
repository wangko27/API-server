package io.nuls.api.entity;

import io.nuls.api.constant.EntityConstant;

import java.util.List;

public class Transaction {

    private Long id;

    private String hash;

    private Integer txIndex;

    private Integer type;

    private Long createTime;

    private Long blockHeight;

    private String remark;

    private Long fee;

    private Integer size;

    private Long amount;

    private List<Input> inputs;

    private List<Output> outputList;

    private List<Utxo> outputs;

    private String scriptSign;

    private TxData txData;

    private List<TxData> txDataList;

    public void caclTx(Transaction tx,String address){
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public Integer getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(Integer txIndex) {
        this.txIndex = txIndex;
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

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getAmount() {
        return amount==null?0L:amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
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

    public String getScriptSign() {
        return scriptSign;
    }

    public void setScriptSign(String scriptSign) {
        this.scriptSign = scriptSign;
    }

    public TxData getTxData() {
        return txData;
    }

    public void setTxData(TxData txData) {
        this.txData = txData;
    }

    public List<TxData> getTxDataList() {
        return txDataList;
    }

    public void setTxDataList(List<TxData> txDataList) {
        this.txDataList = txDataList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Output> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<Output> outputList) {
        this.outputList = outputList;
    }
}