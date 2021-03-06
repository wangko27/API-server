/*
 * *
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
 *
 */
package io.nuls.api.model.tx;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.CancelDeposit;
import io.nuls.api.model.CoinData;
import io.nuls.api.model.Transaction;
import io.nuls.api.utils.NulsByteBuffer;

/**
 * @author Niels
 * @date 2017/12/4
 */
public class CancelDepositTransaction extends Transaction<CancelDeposit> {

    public CancelDepositTransaction() {
        super(EntityConstant.TX_TYPE_CANCEL_DEPOSIT);
    }

    public CancelDepositTransaction(CoinData coinData) {
        super(EntityConstant.TX_TYPE_CANCEL_DEPOSIT);
    }

    @Override
    protected CancelDeposit parseTxData(NulsByteBuffer byteBuffer) throws NulsException {
        return byteBuffer.readNulsData(new CancelDeposit());
    }

    @Override
    public String getInfo(byte[] address) {
//        if (null != this.txData) {
//            LedgerService ledgerService = NulsContext.getServiceBean(LedgerService.class);
//            DepositTransaction tx = (DepositTransaction) ledgerService.getTx(this.txData.getJoinTxHash());
//            if (null != tx) {
//                return "unlock " +tx.getTxData().getDeposit().toCoinString();
//            }
//        }
        return "--";
    }
    @Override
    public boolean isUnlockTx() {
        return true;
    }
}