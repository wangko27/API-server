package io.nuls.api.entity;

import io.nuls.api.model.DeleteContractData;
import io.nuls.sdk.core.utils.AddressTool;

public class ContractDeleteInfo extends TxData {
    private String sender;
    private String contractAddress;

    public ContractDeleteInfo(DeleteContractData delete) {
        this.sender = AddressTool.getStringAddressByBytes(delete.getSender());
        this.contractAddress = AddressTool.getStringAddressByBytes(delete.getContractAddress());
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
}