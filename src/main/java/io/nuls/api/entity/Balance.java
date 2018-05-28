package io.nuls.api.entity;

public class Balance {
    private String address;

    private Long id;

    private Long locked;

    private Long usable;

    private Long blockHeight;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}