/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.nuls.api.model;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.ErrorCode;
import io.nuls.api.crypto.Base58;
import io.nuls.api.crypto.Hex;
import io.nuls.api.exception.NulsRuntimeException;
import io.nuls.api.utils.AddressTool;
import io.nuls.api.utils.SerializeUtils;
import io.nuls.api.utils.log.Log;

/**
 * @author: Chralie
 * @date: 2018/5/4
 */
public class Address {

    /**
     * hash length
     */
    private static final int HASH_LENGTH = 23;

    /**
     * RIPEMD160 length
     */
    private static final int LENGTH = 20;

    /**
     * chain id
     */

    private short chainId = Constant.DEFAULT_CHAIN_ID;

    /**
     * hash160 of public key
     */
    protected byte[] hash160;

    /**
     * @param address bytes
     */

    protected byte[] base58Bytes;

    /**
     * @param address
     */
    public Address(String address) {
        try {
            byte[] bytes = Base58.decode(address);

            Address addressTmp = Address.fromHashs(bytes);
            this.chainId = addressTmp.getChainId();
            this.hash160 = addressTmp.getHash160();
            this.base58Bytes = calcBase58bytes();
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public Address(short chainId, byte[] hash160) {
        this.chainId = chainId;
        this.hash160 = hash160;
        this.base58Bytes = calcBase58bytes();
    }

    public byte[] getHash160() {
        return hash160;
    }

    @Override
    public String toString() {
        return getBase58();
    }

    public short getChainId() {
        return chainId;
    }

    public String getBase58() {
        return Base58.encode(calcBase58bytes());
    }

    public static Address fromHashs(String address) throws Exception {
        byte[] bytes = Base58.decode(address);
        return fromHashs(bytes);
    }

    public static Address fromHashs(byte[] hashs) {
        if (hashs == null || hashs.length != HASH_LENGTH) {
            throw new NulsRuntimeException(ErrorCode.DATA_ERROR);
        }

        short chainId = SerializeUtils.bytes2Short(hashs);
        byte[] content = new byte[LENGTH];
        System.arraycopy(hashs, 2, content, 0, LENGTH);

        Address address = new Address(chainId, content);
        AddressTool.checkXOR(address.calcBase58bytes());
        return address;
    }

    public byte[] calcBase58bytes() {
        byte[] body = new byte[22];
        System.arraycopy(SerializeUtils.shortToBytes(chainId), 0, body, 0, 2);
        System.arraycopy(hash160, 0, body, 2, hash160.length);
        byte xor = getXor(body);
        byte[] base58bytes = new byte[23];
        System.arraycopy(body, 0, base58bytes, 0, body.length);
        base58bytes[body.length] = xor;
        return base58bytes;
    }

    protected byte getXor(byte[] body) {

        byte xor = 0x00;
        for (int i = 0; i < body.length; i++) {
            xor ^= body[i];
        }

        return xor;
    }

    public static boolean validAddress(String address) {
        return AddressTool.validAddress(address);
    }

    public static boolean validAddress(byte[] address) {
        return AddressTool.validAddress(address);
    }

    @Override
    public boolean equals(Object obj) {
        Address other = (Address) obj;
        return this.getBase58().equals(other.getBase58());
    }

    public String hashHex() {
        return Hex.encode(calcBase58bytes());
    }

    public byte[] getBase58Bytes() {
        return base58Bytes;
    }

    public void setBase58Bytes(byte[] base58Bytes) {
        this.base58Bytes = base58Bytes;
    }

    public static int size() {
        return HASH_LENGTH;
    }
}
