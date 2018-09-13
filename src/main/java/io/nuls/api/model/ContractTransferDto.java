/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.api.model;

import io.nuls.api.server.dto.contract.ContractTransfer;
import io.nuls.sdk.core.utils.AddressTool;

/**
 * @author: PierreLuo
 */
public class ContractTransferDto {

    private String txHash;
    private String from;
    private String to;
    private long value;
    private long fee;
    private boolean isSendBack;
    private String orginTxHash;

    public ContractTransferDto() {
    }

    public ContractTransferDto(ContractTransfer transfer) {
        this.from = AddressTool.getStringAddressByBytes(transfer.getFrom());
        this.to = AddressTool.getStringAddressByBytes(transfer.getTo());
        this.value = transfer.getValue().getValue();
        this.fee = transfer.getFee().getValue();
        this.isSendBack = transfer.isSendBack();
        NulsDigestData thatHash = transfer.getHash();
        this.txHash = thatHash == null ? null : thatHash.getDigestHex();
        NulsDigestData thatOrginTxHash = transfer.getOrginHash();
        this.orginTxHash = thatOrginTxHash == null ? null : thatOrginTxHash.getDigestHex();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public boolean isSendBack() {
        return isSendBack;
    }

    public void setSendBack(boolean sendBack) {
        isSendBack = sendBack;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getOrginTxHash() {
        return orginTxHash;
    }

    public void setOrginTxHash(String orginTxHash) {
        this.orginTxHash = orginTxHash;
    }
}
