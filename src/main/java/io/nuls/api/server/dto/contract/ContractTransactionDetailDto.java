/*
 *
 *  * MIT License
 *  *
 *  * Copyright (c) 2017-2018 nuls.io
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package io.nuls.api.server.dto.contract;

import io.nuls.api.entity.*;

import java.util.List;

public class ContractTransactionDetailDto {

    private String hash;

    private String contractAddress;

    private Integer type;

    private Long createTime;

    private Long blockHeight;

    private String remark;

    private Long fee;

    private Integer size;

    private Long amount;

    private List<Input> inputs;

    private List<Utxo> outputs;

    private String scriptSign;

    private String status;

    private Integer confirmCount;

    private TxData txData;

    private ContractResultInfoDto resultDto;

    public ContractTransactionDetailDto(Transaction transaction) {
        this.amount = transaction.getAmount();
        this.blockHeight = transaction.getBlockHeight();
        this.createTime = transaction.getCreateTime();
        this.fee = transaction.getFee();
        this.hash = transaction.getHash();
        this.inputs = transaction.getInputs();
        this.outputs = transaction.getOutputs();
        this.remark = transaction.getRemark();
        this.scriptSign = transaction.getScriptSign();
        this.size = transaction.getSize();
        this.type = transaction.getType();
        this.txData = transaction.getTxData();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(Integer confirmCount) {
        this.confirmCount = confirmCount;
    }

    public ContractResultInfoDto getResultDto() {
        return resultDto;
    }

    public void setResultDto(ContractResultInfoDto resultDto) {
        this.resultDto = resultDto;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash == null ? null : hash.trim();
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getAmount() {
        return amount==null?0L:amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    public List<Utxo> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Utxo> outputs) {
        this.outputs = outputs;
    }

    public String getScriptSign() {
        return scriptSign;
    }

    public void setScriptSign(String scriptSign) {
        this.scriptSign = scriptSign;
    }

    public TxData getTxData() {
        return txData;
    }

    public void setTxData(TxData txData) {
        this.txData = txData;
    }

}