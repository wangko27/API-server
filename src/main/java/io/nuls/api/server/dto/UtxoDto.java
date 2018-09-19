package io.nuls.api.server.dto;

public class UtxoDto {

    private String address;

    private Long total=0L;

    public UtxoDto() {

    }
    public UtxoDto(String address,long total) {
        this.address = address;
        this.total = total;
    }
    public UtxoDto(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
