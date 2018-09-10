package io.nuls.api.entity;

public class ContrackAddressInfo {
    private String contractAddress;

    private String creater;

    private String createTxHash;

    private Long blockHeight;

    private Integer isNrc20;

    private Integer status;

    private Long createTime;

    private String deleteHash;

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

    public String getCreateTxHash() {
        return createTxHash;
    }

    public void setCreateTxHash(String createTxHash) {
        this.createTxHash = createTxHash == null ? null : createTxHash.trim();
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Integer getIsNrc20() {
        return isNrc20;
    }

    public void setIsNrc20(Integer isNrc20) {
        this.isNrc20 = isNrc20;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getDeleteHash() {
        return deleteHash;
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash == null ? null : deleteHash.trim();
    }
}