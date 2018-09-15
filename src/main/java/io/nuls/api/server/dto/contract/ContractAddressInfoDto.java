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
package io.nuls.api.server.dto.contract;

import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.server.dto.contract.vm.ProgramMethod;
import io.nuls.api.utils.DateUtil;

import java.util.Date;
import java.util.List;

public class ContractAddressInfoDto {
    private String contractAddress;

    private String creater;

    private String createTxHash;

    private Long blockHeight;

    private Integer isNrc20;

    private Integer status;

    private String createTime;

    private String deleteHash;

    private String methods;

    private String tokenName;

    private String symbol;

    private Long decimals;

    private String totalsupply;

    private Long balance;
    private int txCount;
    private List<ProgramMethod> method;

    public ContractAddressInfoDto() {
    }

    public ContractAddressInfoDto(ContractAddressInfo contractInfo) {
        this.contractAddress = contractInfo.getContractAddress();
        this.creater = contractInfo.getCreater();
        this.createTxHash = contractInfo.getCreateTxHash();
        this.createTime = DateUtil.convertDate(new Date(contractInfo.getCreateTime()));
        this.blockHeight = contractInfo.getBlockHeight();
        this.isNrc20 = contractInfo.getIsNrc20();
        this.status = contractInfo.getStatus();
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress == null ? null : contractAddress.trim();
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    public String getCreateTxHash() {
        return createTxHash;
    }

    public void setCreateTxHash(String createTxHash) {
        this.createTxHash = createTxHash == null ? null : createTxHash.trim();
    }

    public Long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Integer getIsNrc20() {
        return isNrc20;
    }

    public void setIsNrc20(Integer isNrc20) {
        this.isNrc20 = isNrc20;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeleteHash() {
        return deleteHash;
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash == null ? null : deleteHash.trim();
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getDecimals() {
        return decimals;
    }

    public void setDecimals(Long decimals) {
        this.decimals = decimals;
    }

    public String getTotalsupply() {
        return totalsupply;
    }

    public void setTotalsupply(String totalsupply) {
        this.totalsupply = totalsupply;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public int getTxCount() {
        return txCount;
    }

    public void setTxCount(int txCount) {
        this.txCount = txCount;
    }

    public List<ProgramMethod> getMethod() {
        return method;
    }

    public void setMethod(List<ProgramMethod> method) {
        this.method = method;
    }
}