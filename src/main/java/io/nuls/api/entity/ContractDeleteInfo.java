package io.nuls.api.entity;

import io.nuls.api.model.DeleteContractData;
import io.nuls.sdk.core.utils.AddressTool;

public class ContractDeleteInfo extends TxData {
    private String createTxHash;

    private String contractAddress;

    private String creater;

    public ContractDeleteInfo(DeleteContractData delete) {
        this.creater = AddressTool.getStringAddressByBytes(delete.getSender());
        this.contractAddress = AddressTool.getStringAddressByBytes(delete.getContractAddress());
    }

    public String getCreateTxHash() {
        return createTxHash;
    }

    public void setCreateTxHash(String createTxHash) {
        this.createTxHash = createTxHash;
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