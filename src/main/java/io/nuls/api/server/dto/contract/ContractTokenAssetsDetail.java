package io.nuls.api.server.dto.contract;

import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.ContractTokenTransferInfo;

import java.util.List;

public class ContractTokenAssetsDetail {
    private String accountAddress;

    private String contractAddress;

    private String amount;

    private String hash;

    private PageInfo<ContractTokenTransferInfo> page;

    public PageInfo<ContractTokenTransferInfo> getPage() {
        return page;
    }

    public void setPage(PageInfo<ContractTokenTransferInfo> page) {
        this.page = page;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress == null ? null : accountAddress.trim();
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress == null ? null : contractAddress.trim();
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount == null ? null : amount.trim();
    }
}