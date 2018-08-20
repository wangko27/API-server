package io.nuls.api.server.dto;

import io.nuls.api.model.Na;

/**
 * Description:
 * Author: zsj
 * Date:  2018/8/17 0017
 */
public class TransFeeDto {
    private Na na = Na.ZERO;
    private int type = 1;//1正常 2零钱太多，无法全部转账 3钱不够

    public Na getNa() {
        return na;
    }

    public void setNa(Na na) {
        this.na = na;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void tooManyUtxo(){
        this.type = 2;
    }
}
