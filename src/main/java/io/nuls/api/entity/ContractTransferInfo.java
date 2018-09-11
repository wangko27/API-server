/**
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
 */
package io.nuls.api.entity;


import io.nuls.api.exception.NulsException;
import io.nuls.api.model.ContractData;
import io.nuls.api.model.NulsDigestData;
import io.nuls.api.model.TransactionLogicData;
import io.nuls.api.utils.NulsByteBuffer;
import io.nuls.api.utils.NulsOutputStreamBuffer;
import io.nuls.api.utils.SerializeUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: PierreLuo
 */
public class ContractTransferInfo extends TxData{

    private NulsDigestData orginTxHash;
    private byte[] contractAddress;
    private byte success;

    public ContractTransferInfo(){

    }

    public ContractTransferInfo(NulsDigestData orginTxHash, byte[] contractAddress, byte success) {
        this.orginTxHash = orginTxHash;
        this.contractAddress = contractAddress;
        this.success = success;
    }

    public byte getSuccess() {
        return success;
    }

    public void setSuccess(byte success) {
        this.success = success;
    }

    public byte[] getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(byte[] contractAddress) {
        this.contractAddress = contractAddress;
    }

    public NulsDigestData getOrginTxHash() {
        return orginTxHash;
    }

    public void setOrginTxHash(NulsDigestData orginTxHash) {
        this.orginTxHash = orginTxHash;
    }

    public long getGasLimit() {
        return 0L;
    }

    public byte[] getSender() {
        return null;
    }

    public long getPrice() {
        return 0L;
    }
}
