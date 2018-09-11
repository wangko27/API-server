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
import io.nuls.api.model.CreateContractData;
import io.nuls.api.model.TransactionLogicData;
import io.nuls.api.utils.NulsByteBuffer;
import io.nuls.api.utils.NulsOutputStreamBuffer;
import io.nuls.api.utils.SerializeUtils;
import io.nuls.sdk.core.crypto.Hex;
import io.nuls.sdk.core.utils.AddressTool;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: PierreLuo
 */
public class ContractCreateInfo extends TxData{

    private String sender;
    private String contractAddress;
    private long value;
    private String hexCode;
    private long gasLimit;
    private long price;
    private byte argsCount;
    private String[][] args;

    public ContractCreateInfo(CreateContractData create) {
        this.sender = AddressTool.getStringAddressByBytes(create.getSender());
        this.contractAddress = AddressTool.getStringAddressByBytes(create.getContractAddress());
        this.value = create.getValue();
        this.hexCode = Hex.encode(create.getCode());
        this.gasLimit = create.getGasLimit();
        this.price = create.getPrice();
        this.argsCount = create.getArgsCount();
        this.args = create.getArgs();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getHexCode() {
        return hexCode;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    public long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public byte getArgsCount() {
        return argsCount;
    }

    public void setArgsCount(byte argsCount) {
        this.argsCount = argsCount;
    }

    public String[][] getArgs() {
        return args;
    }

    public void setArgs(String[][] args) {
        this.args = args;
    }


}
