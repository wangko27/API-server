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
package io.nuls.api.model.tx;

import io.nuls.api.constant.ContractConstant;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.CallContractData;
import io.nuls.api.model.Coin;
import io.nuls.api.model.Na;
import io.nuls.api.model.Transaction;
import io.nuls.api.server.dto.contract.ContractResult;
import io.nuls.api.utils.NulsByteBuffer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Desription:
 * @Author: PierreLuo
 * @Date: 2018/4/20
 */
public class CallContractTransaction extends Transaction<CallContractData> implements ContractTransaction{

    private ContractResult contractResult;

    private transient Collection<ContractTransferTransaction> contractTransferTxs;

    private transient Na returnNa;

    private transient Object programExecutor;

    public CallContractTransaction() {
        super(ContractConstant.TX_TYPE_CALL_CONTRACT);
    }

    @Override
    protected CallContractData parseTxData(NulsByteBuffer byteBuffer) throws NulsException {
        return byteBuffer.readNulsData(new CallContractData());
    }

    @Override
    public String getInfo(byte[] address) {
        boolean isTransfer = false;
        Coin to = coinData.getTo().get(0);
        if (!Arrays.equals(address, to.getOwner())) {
            isTransfer = true;
        }
        if (isTransfer) {
            return "-" + to.getNa().add(getFee()).toCoinString();
        } else {
            return "-" + getFee().toCoinString();
        }
    }

    @Override
    public ContractResult getContractResult() {
        return contractResult;
    }

    @Override
    public void setContractResult(ContractResult contractResult) {
        this.contractResult = contractResult;
    }

    @Override
    public void setReturnNa(Na returnNa) {
        this.returnNa = returnNa;
    }

    @Override
    public Na getFee() {
        Na resultFee = super.getFee();
        if(returnNa != null) {
            resultFee = resultFee.minus(returnNa);
        }
        return resultFee;
    }

    @Override
    public Object getProgramExecutor() {
        return programExecutor;
    }

    @Override
    public void setProgramExecutor(Object programExecutor) {
        this.programExecutor = programExecutor;
    }


    public List<byte[]> getAllRelativeAddress() {
        /*if(contractResult == null && contractService != null) {
            contractResult = contractService.getContractExecuteResult(this.hash);
        }
        if(contractResult != null) {
            List<byte[]> relativeAddress = super.getAllRelativeAddress();
            if(relativeAddress == null) {
                return new ArrayList<>();
            }
            if(relativeAddress.size() == 0) {
                return relativeAddress;
            }
            HashSet<ByteArrayWrapper> addressesSet = MapUtil.createHashSet(relativeAddress.size());
            relativeAddress.stream().forEach(address -> addressesSet.add(new ByteArrayWrapper(address)));

            // 合约内部转账
            List<ContractTransfer> transfers = contractResult.getTransfers();
            if(transfers != null && transfers.size() > 0) {
                for(ContractTransfer transfer : transfers) {
                    addressesSet.add(new ByteArrayWrapper(transfer.getTo()));
                }
            }

            List<byte[]> resultList = new ArrayList<>();
            for(ByteArrayWrapper wrapper : addressesSet) {
                resultList.add(wrapper.getBytes());
            }
            return resultList;
        } else {
            return super.getAllRelativeAddress();
        }*/
        return null;
    }

    public void setContractTransferTxs(Collection<ContractTransferTransaction> contractTransferTxs) {
        this.contractTransferTxs = contractTransferTxs;
    }

    public Collection<ContractTransferTransaction> getContractTransferTxs() {
        return contractTransferTxs;
    }
}
