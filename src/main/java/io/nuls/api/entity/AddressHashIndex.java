package io.nuls.api.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description:
 * Author: moon
 * Date:  2018/7/11 0011
 */
public class AddressHashIndex {
    private String address;
    private Set<String> hashIndexSet = new HashSet<>();

    public AddressHashIndex(){

    }

    public AddressHashIndex(String address,Set<String> hashIndexSet){
        this.address = address;
        this.hashIndexSet = hashIndexSet;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<String> getHashIndexSet() {
        return hashIndexSet;
    }

    public void setHashIndexSet(Set<String> hashIndexSet) {
        this.hashIndexSet = hashIndexSet;
    }
}
