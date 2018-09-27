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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.nuls.api.entity.ContractResultInfo;
import io.nuls.api.entity.ContractTransferInfo;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author: PierreLuo
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractResultInfoDto {
    private String txHash;

    private String contractAddress;

    private String success;

    private String errorMessage;

    private String result;

    private Long gasLimit;

    private Long gasUsed;

    private Long price;

    private Long totalFee;

    private Long txSizeFee;

    private Long actualContractFee;

    private Long refundFee;

    private String stateroot;

    private Long txValue;

    private String stacktrace;

    private Long balance;

    private Long nonce;

    private List<ContractTransferInfo> transfers;

    private String events;

    private List<SimpleContractTokenTransferInfoDto> tokenTransfers;

    private String tokenName;

    private String symbol;

    private Long decimals;

    private String remark;

    private Long createTime;

    public static ContractResultInfoDto parse(ContractResultInfo info) {
        ContractResultInfoDto dto = new ContractResultInfoDto();
        try {
            BeanUtils.copyProperties(info, dto);
            List<SimpleContractTokenTransferInfoDto> list = JSONUtils.json2list(info.getTokenTransfers(), SimpleContractTokenTransferInfoDto.class);
            dto.setTokenTransfers(list);
        } catch (Exception e){
            Log.error("ContractResultInfoDto Parse Error!" + e.getMessage());
        }
        return dto;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash == null ? null : txHash.trim();
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress == null ? null : contractAddress.trim();
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success == null ? null : success.trim();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? null : errorMessage.trim();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    public Long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(Long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public Long getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(Long gasUsed) {
        this.gasUsed = gasUsed;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public Long getTxSizeFee() {
        return txSizeFee;
    }

    public void setTxSizeFee(Long txSizeFee) {
        this.txSizeFee = txSizeFee;
    }

    public Long getActualContractFee() {
        return actualContractFee;
    }

    public void setActualContractFee(Long actualContractFee) {
        this.actualContractFee = actualContractFee;
    }

    public Long getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Long refundFee) {
        this.refundFee = refundFee;
    }

    public String getStateroot() {
        return stateroot;
    }

    public void setStateroot(String stateroot) {
        this.stateroot = stateroot == null ? null : stateroot.trim();
    }

    public Long getTxValue() {
        return txValue;
    }

    public void setTxValue(Long txValue) {
        this.txValue = txValue;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace == null ? null : stacktrace.trim();
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public List<ContractTransferInfo> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<ContractTransferInfo> transfers) {
        this.transfers = transfers;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events == null ? null : events.trim();
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName == null ? null : tokenName.trim();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public Long getDecimals() {
        return decimals;
    }

    public void setDecimals(Long decimals) {
        this.decimals = decimals;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getCreateTime() {
        return createTime;
    }

    public List<SimpleContractTokenTransferInfoDto> getTokenTransfers() {
        return tokenTransfers;
    }

    public void setTokenTransfers(List<SimpleContractTokenTransferInfoDto> tokenTransfers) {
        this.tokenTransfers = tokenTransfers;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
