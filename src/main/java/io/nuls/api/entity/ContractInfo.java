package io.nuls.api.entity;

/**
 * Created by inchain on 2018/9/25.
 */
public class ContractInfo extends ContractAddressInfo {
    private Long balance;
    public void setBalance(Long balance) {
        this.balance = balance;
    }
    public Long getBalance() {
        return this.balance;
    }
}
