package io.nuls.api.model;

import io.nuls.api.exception.NulsException;
import io.nuls.api.utils.NulsByteBuffer;
import io.nuls.api.utils.NulsOutputStreamBuffer;
import io.nuls.api.utils.SerializeUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: Charlie
 * @date: 2018/5/9
 */
public class Alias extends TransactionLogicData {

    private byte[] address;


    private String alias;


    public Alias() {
    }

    public Alias(byte[] address, String alias) {
        this.address = address;
        this.alias = alias;
    }

    public byte[] getAddress() {
        return address;
    }

    public void setAddress(byte[] address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }


    @Override
    public Set<byte[]> getAddresses() {
        Set<byte[]> addressSet = new HashSet<>();
        addressSet.add(this.address);
        return addressSet;
    }

    @Override
    public int size() {
        int s = 0;
        s += SerializeUtils.sizeOfBytes(address);
        s += SerializeUtils.sizeOfString(alias);
        return s;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeBytesWithLength(address);
        stream.writeString(alias);
    }

    @Override
    protected void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.address = byteBuffer.readByLengthByte();
        this.alias = byteBuffer.readString();

    }
}
