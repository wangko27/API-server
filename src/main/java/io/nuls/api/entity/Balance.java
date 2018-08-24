package io.nuls.api.entity;

public class Balance {
    public Balance(){

    }
    public Balance(String address,Long locked,Long usable){
        this.address = address;
        this.locked = locked;
        this.usable = usable;
    }
    private Long id;

    private String address;

    private Long locked;

    private Long usable;

    private Long blockHeight;

    private String assetsCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Long getLocked() {
        return locked;
    }

    public void setLocked(Long locked) {
        this.locked = locked;
    }

    public Long getUsable() {
        return usable;
    }

    public void setUsable(Long usable) {
        this.usable = usable;
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getAssetsCode() {
        return assetsCode;
    }

    public void setAssetsCode(String assetsCode) {
        this.assetsCode = assetsCode == null ? null : assetsCode.trim();
    }
}