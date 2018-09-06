package io.nuls.api.server.dto;

import io.nuls.api.model.Na;

/**
 * Description:
 * Author: moon
 * Date:  2018/8/17 0017
 */
public class TransFeeDto {
    private Na na = Na.ZERO;
    //交易大小
    private int size = 1;
    //最多可转多少
    private Na money = Na.ZERO;

    public Na getNa() {
        return na;
    }

    public void setNa(Na na) {
        this.na = na;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Na getMoney() {
        return money;
    }

    public void setMoney(Na money) {
        this.money = money;
    }
}
