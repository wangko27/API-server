package io.nuls.api.entity;

import io.nuls.api.crypto.Hex;
import io.nuls.api.model.CreateContractData;
import io.nuls.api.utils.AddressTool;
import io.nuls.api.utils.JSONUtils;

public class ContrackCreateInfo extends TxData {
    private String createTxHash;

    private String contractAddress;

    private String creater;

    private String contractCode;

    private Long gaslimit;

    private Long price;

    private String args;

    private String methods;

    private Long createTime;

    public ContrackCreateInfo() {
    }

    public ContrackCreateInfo(CreateContractData create) {
        this.creater = AddressTool.getStringAddressByBytes(create.getSender());
        this.contractAddress = AddressTool.getStringAddressByBytes(create.getContractAddress());
        this.contractCode = Hex.encode(create.getCode());
        this.gaslimit = create.getGasLimit();
        this.price = create.getPrice();
        try {
            this.args = JSONUtils.obj2json(create.getArgs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCreateTxHash() {
        return createTxHash;
    }

    public void setCreateTxHash(String createTxHash) {
        this.createTxHash = createTxHash == null ? null : createTxHash.trim();
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress == null ? null : contractAddress.trim();
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode == null ? null : contractCode.trim();
    }

    public Long getGaslimit() {
        return gaslimit;
    }

    public void setGaslimit(Long gaslimit) {
        this.gaslimit = gaslimit;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args == null ? null : args.trim();
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods == null ? null : methods.trim();
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}