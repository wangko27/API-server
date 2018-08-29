package io.nuls.api.server.dto;

import io.nuls.api.model.Na;

/**
 * Description:
 * Author: zsj
 * Date:  2018/8/17 0017
 */
public class TransFeeDto {
    private Na na = Na.ZERO;
    private int size = 1;//交易大小

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
}
