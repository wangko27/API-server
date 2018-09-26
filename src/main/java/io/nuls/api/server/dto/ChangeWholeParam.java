package io.nuls.api.server.dto;

/**
 * Description:
 * Author: moon
 * Date:  2018/7/24 0024
 */
public class ChangeWholeParam {
    /**
     * 转账地址
     */
    private String address;
    /**
     * 备注
     */
    private String remark;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
