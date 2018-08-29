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
package io.nuls.api.model.tx;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.Coin;
import io.nuls.api.model.Na;
import io.nuls.api.model.Transaction;
import io.nuls.api.model.TransactionLogicData;
import io.nuls.api.utils.AddressTool;
import io.nuls.api.utils.NulsByteBuffer;

import java.util.Arrays;

/**
 * @author Niels
 * @date 2017/12/4
 */
public class CoinBaseTransaction extends Transaction {

    public CoinBaseTransaction() {
        this(EntityConstant.TX_TYPE_COINBASE);
    }

    @Override
    protected TransactionLogicData parseTxData(NulsByteBuffer byteBuffer) throws NulsException {
        byteBuffer.readBytes(Constant.PLACE_HOLDER.length);
        return null;
    }

    protected CoinBaseTransaction(int type) {
        super(type);
    }

    @Override
    public String getInfo(byte[] address) {
        Na to = Na.ZERO;
        for (Coin coin : coinData.getTo()) {
            if (Arrays.equals(address, coin.getOwner())) {
                to = to.add(coin.getNa());
            }
        }
        return "+" + to.toText();
    }


    @Override
    public boolean isFreeOfFee() {
        return true;
    }

    @Override
    public boolean needVerifySignature() {
        return false;
    }
}
