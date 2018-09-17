package io.nuls.api.server.dto;

import io.nuls.api.entity.ContractTokenInfo;

/**
 * Created by inchain on 2018/9/17.
 */
public class ContractTokenDto extends  ContractTokenInfo{
    private Integer status;

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
