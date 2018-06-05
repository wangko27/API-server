package io.nuls.api.server.dto;

import io.nuls.api.entity.Utxo;

public class UtxoDto extends Utxo {



    private Long total;

    public UtxoDto() {

    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
