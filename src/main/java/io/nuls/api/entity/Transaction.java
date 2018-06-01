package io.nuls.api.entity;

import java.util.List;

public class Transaction {

    private String hash;

    private Integer txIndex;

    private Integer type;

    private Long createTime;

    private Long blockHeight;

    private String remark;

    private Long fee;

    private Integer size;

    private Long amount;

    private byte[] extend;

    private List<Input> inputs;

    private List<Utxo> outputs;

    private String scriptSign;

    private TxData txData;

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
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public byte[] getExtend() {
        return extend;
    }

    public void setExtend(byte[] extend) {
        this.extend = extend;
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
}