package io.nuls.api.server.dto;

import io.nuls.api.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: zsj
 * Date:  2018/6/10 0010
 */
public class TransactionDto{
    private Long confirmCount = 0L;

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

    private List<Output> outputList;

    private List<Utxo> outputs;

    private String scriptSign;

    private List<TxData> txDataList;

    public TransactionDto(){

    }
    public TransactionDto(Transaction transaction){
        this.amount = transaction.getAmount();
        this.blockHeight = transaction.getBlockHeight();
        this.createTime = transaction.getCreateTime();
        this.fee = transaction.getFee();
        this.hash = transaction.getHash();
        this.scriptSign = transaction.getScriptSign();
        this.size = transaction.getSize();
        this.outputList = transaction.getOutputList();
        this.outputs = transaction.getOutputs();
        this.inputs = transaction.getInputs();
        this.type = transaction.getType();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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
        this.remark = remark;
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

    public List<Output> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<Output> outputList) {
        this.outputList = outputList;
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

    public List<TxData> getTxDataList() {
        return txDataList;
    }

    public void setTxDataList(List<TxData> txDataList) {
        this.txDataList = txDataList;
    }

    public Long getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(Long confirmCount) {
        this.confirmCount = confirmCount;
    }
}
