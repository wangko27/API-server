package io.nuls.api.entity;

public class ContractTokenInfo {
    private String txHash;

    private String contractAddress;

    private String tokenName;

    private String symbol;

    private Long decimals;

    private Long totalsupply;

    private Long createTime;

    public String getTxHash() {
        return txHash;
    }

    public ContractTokenInfo() {
    }

    public ContractTokenInfo(ContractAddressInfo contractAddressInfo) {
        contractAddress = contractAddressInfo.getContractAddress();
        txHash = contractAddressInfo.getCreateTxHash();
        createTime = contractAddressInfo.getCreateTime();
        tokenName = contractAddressInfo.getTokenName();
        symbol = contractAddressInfo.getSymbol();
        decimals = contractAddressInfo.getDecimals();
        totalsupply = contractAddressInfo.getTotalsupply();
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress == null ? null : contractAddress.trim();
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName == null ? null : tokenName.trim();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public Long getDecimals() {
        return decimals;
    }

    public void setDecimals(Long decimals) {
        this.decimals = decimals;
    }

    public Long getTotalsupply() {
        return totalsupply;
    }

    public void setTotalsupply(Long totalsupply) {
        this.totalsupply = totalsupply;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}