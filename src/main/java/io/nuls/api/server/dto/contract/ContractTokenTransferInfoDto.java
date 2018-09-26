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

import io.nuls.api.entity.ContractTokenInfo;
import io.nuls.api.entity.ContractTokenTransferInfo;
import io.nuls.api.model.ContractTokenTransferDto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 代币转账信息DTO,包含代币名称、符号等信息
 */
public class ContractTokenTransferInfoDto {
    private String contractAddress;
    private String name;
    private String symbol;
    private Long decimals;
    private String fromAddress;
    private String toAddress;
    private BigInteger txValue;
    private Long createTime;
    private String txHash;

    public ContractTokenTransferInfoDto() {
    }

    public ContractTokenTransferInfoDto(ContractTokenTransferInfo info) {
        this.setContractAddress(info.getContractAddress());
        this.setCreateTime(info.getCreateTime());
        this.setFromAddress(info.getFromAddress());
        this.setToAddress(info.getToAddress());
        this.setTxHash(info.getTxHash());
        this.setTxValue(info.getTxValue());
    }

    public static List<ContractTokenTransferInfoDto> parseList(List<ContractTokenTransferInfo> datas, ContractTokenInfo contractTokenInfo){
        String name = contractTokenInfo.getTokenName();
        String symbol = contractTokenInfo.getSymbol();
        Long decimals = contractTokenInfo.getDecimals();
        List<ContractTokenTransferInfoDto> contractTokenTransferInfoDtos = new ArrayList<>();
        for (ContractTokenTransferInfo data : datas) {
            ContractTokenTransferInfoDto dto = new ContractTokenTransferInfoDto(data);
            dto.setName(name);
            dto.setSymbol(symbol);
            dto.setDecimals(decimals);
            contractTokenTransferInfoDtos.add(dto);
        }
        return contractTokenTransferInfoDtos;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public BigInteger getTxValue() {
        return txValue;
    }

    public void setTxValue(BigInteger txValue) {
        this.txValue = txValue;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
}