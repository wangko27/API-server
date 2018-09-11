package io.nuls.api.entity;

import io.nuls.api.model.DeleteContractData;
import io.nuls.sdk.core.utils.AddressTool;

public class ContractDeleteInfo extends TxData {
    private String creater;
    private String contractAddress;
    private String txHash;

    public ContractDeleteInfo(DeleteContractData delete) {
        this.creater = AddressTool.getStringAddressByBytes(delete.getSender());
        this.contractAddress = AddressTool.getStringAddressByBytes(delete.getContractAddress());
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
}