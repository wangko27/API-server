package io.nuls.api.server.dto;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/24 0024
 */
public class TransactionParam {
    /**
     * 转账地址
     */
    private String address;
    /**
     * 收款地址
     */
    private String toAddress;
    /**
     * 备注
     */
    private String remark;
    /**
     * 别名
     */
    private String alias;
    /**
     * 节点创建的交易hash
     */
    private String agentHash;
    /**
     * 金额(转账金额或者委托金额)
     */
    private long money;
    /**
     * 单价
     */
    private long price;

    /**
     * 类型 types 1.转账 2.设置别名 3.加入共识 4.退出共识
     */
    private int types;

    /**
     * 签名串
     */
    private String sign;

    /**
     * 组装完成交易包之后，发送给前段
     */
    private String serializ;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setAgentHash(String agentHash) {
        this.agentHash = agentHash;
    }

    public String getAgentHash() {
        return agentHash;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getTypes() {
        return types;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSerializ() {
        return serializ;
    }

    public void setSerializ(String serializ) {
        this.serializ = serializ;
    }
}
