package io.nuls.api.entity;

import io.nuls.api.model.ContractTokenTransferDto;

import java.math.BigInteger;

public class ContractTokenTransferInfo {
    private String txHash;

    private String fromAddress;

    private String toAddress;

    private BigInteger txValue;

    private Long createTime;

    private String contractAddress;

    public ContractTokenTransferInfo() {
    }
    public ContractTokenTransferInfo(ContractTokenTransferDto dto) {
        this.fromAddress = dto.getFrom();
        this.toAddress = dto.getTo();
        this.txValue = dto.getValue();
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress == null ? null : fromAddress.trim();
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress == null ? null : toAddress.trim();
    }

    public BigInteger getTxValue() {
        return txValue;
    }

    public void setTxValue(BigInteger txValue) {
        this.txValue = txValue;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}