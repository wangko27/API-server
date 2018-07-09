package io.nuls.api.entity;

import io.nuls.api.utils.JSONUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private byte[] extend;

    private List<Input> inputs;

    private List<Output> outputList;

    private List<Utxo> outputs;

    private String scriptSign;

    private TxData txData;

    private List<TxData> txDataList;

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

    public void transferExtend() throws Exception {
        String extend = new String(this.getExtend());
        Map dataMap = JSONUtils.json2map(extend);
        this.setScriptSign((String) dataMap.get("scriptSign"));
        List<Map> mapList = (List<Map>) dataMap.get("inputs");
        List<Input> inputs = new ArrayList<>();
        for (Map map : mapList) {
            Input input = new Input();
            input.setFromHash((String) map.get("fromHash"));
            input.setFromIndex((Integer) map.get("fromIndex"));
            input.setValue(Long.parseLong(map.get("value").toString()));
            input.setAddress((String) map.get("address"));
            inputs.add(input);
        }

        List<Output> outputList = new ArrayList<>();
        mapList = (List<Map>) dataMap.get("outputs");

        Output output = null;
        for(int i=0;i<mapList.size();i++) {
            output = new Output();
            output.setTxIndex(i);
            output.setTxHash((String) mapList.get(i).get("txHash"));
            output.setAddress((String) mapList.get(i).get("address"));
            output.setValue(Long.parseLong(mapList.get(i).get("value").toString()));
            outputList.add(output);
        }

        for (Map map : mapList) {

        }
        this.inputs = inputs;
        this.setOutputList(outputList);
    }

    public List<Output> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<Output> outputList) {
        this.outputList = outputList;
    }
}