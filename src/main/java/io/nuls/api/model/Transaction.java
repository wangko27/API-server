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
package io.nuls.api.model;

import io.nuls.api.constant.Constant;
import io.nuls.api.crypto.UnsafeByteArrayOutputStream;
import io.nuls.api.exception.NulsException;
import io.nuls.api.utils.NulsByteBuffer;
import io.nuls.api.utils.NulsOutputStreamBuffer;
import io.nuls.api.utils.SerializeUtils;
import io.nuls.api.utils.log.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Niels
 * @date 2017/10/30
 */
public abstract class Transaction<T extends TransactionLogicData> extends BaseNulsData implements Cloneable {

    protected int type;

    protected CoinData coinData;

    protected T txData;

    protected long time;

    private byte[] scriptSig;

    protected byte[] remark;

    protected transient NulsDigestData hash;

    protected long blockHeight = -1L;

//    protected transient TxStatusEnum status = TxStatusEnum.UNCONFIRM;

    protected transient int size;

    @Override
    public int size() {
        int size = 0;
        size += SerializeUtils.sizeOfUint16(); // type
        size += SerializeUtils.sizeOfUint48(); // time
        size += SerializeUtils.sizeOfBytes(remark);
        size += SerializeUtils.sizeOfNulsData(txData);
        size += SerializeUtils.sizeOfNulsData(coinData);
        size += SerializeUtils.sizeOfBytes(scriptSig);
        return size;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeUint16(type);
        stream.writeUint48(time);
        stream.writeBytesWithLength(remark);
        stream.writeNulsData(txData);
        stream.writeNulsData(coinData);
        stream.writeBytesWithLength(scriptSig);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        type = byteBuffer.readUint16();
        time = byteBuffer.readUint48();
        this.remark = byteBuffer.readByLengthByte();
        txData = this.parseTxData(byteBuffer);
        this.coinData = byteBuffer.readNulsData(new CoinData());
        try {
            hash = NulsDigestData.calcDigestData(this.serializeForHash());
        } catch (IOException e) {
            Log.error(e);
        }
        scriptSig = byteBuffer.readByLengthByte();
    }

    public void parseWithoutSing(NulsByteBuffer byteBuffer) throws NulsException {
        type = Integer.parseInt(byteBuffer.readVarInt()+"");
        time = byteBuffer.readVarInt();
        this.remark = byteBuffer.readByLengthByte();
        txData = this.parseTxData(byteBuffer);
        this.coinData = byteBuffer.readNulsData(new CoinData());
        try {
            hash = NulsDigestData.calcDigestData(this.serializeForHash());
        } catch (IOException e) {
            Log.error(e);
        }
        scriptSig = byteBuffer.readByLengthByte();
    }

    public boolean isFreeOfFee() {
        return false;
    }

    public boolean isUnlockTx() {
        return false;
    }

    public boolean needVerifySignature() {
        return true;
    }

    protected abstract T parseTxData(NulsByteBuffer byteBuffer) throws NulsException;

    public Transaction(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public byte[] getRemark() {
        return remark;
    }

    public void setRemark(byte[] remark) {
        this.remark = remark;
    }

    public NulsDigestData getHash() {
        if(hash == null) {
            try {
                hash = NulsDigestData.calcDigestData(serializeForHash());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hash;
    }

    public void setHash(NulsDigestData hash) {
        this.hash = hash;
    }

    public byte[] getScriptSig() {
        return scriptSig;
    }

    public void setScriptSig(byte[] scriptSig) {
        this.scriptSig = scriptSig;
    }

    public T getTxData() {
        return txData;
    }

    public void setTxData(T txData) {
        this.txData = txData;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

//    public TxStatusEnum getStatus() {
//        return status;
//    }
//
//    public void setStatus(TxStatusEnum status) {
//        this.status = status;
//    }

    public CoinData getCoinData() {
        return coinData;
    }

    public void setCoinData(CoinData coinData) {
        this.coinData = coinData;
    }

    public int getSize() {
        if (size == 0) {
            size = size();
        }
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Na getFee() {
        if (isFreeOfFee()) {
            return Na.ZERO;
        }
        Na fee = Na.ZERO;
        if (null != coinData) {
            fee = coinData.getFee();
        }
        return fee;
    }

//    public byte[] getAddressFromSig() {
//        return AddressTool.getAddress(scriptSig);
//    }

//    public List<byte[]> getAllRelativeAddress() {
//        Set<byte[]> addresses = new HashSet<>();
//
//        if (coinData != null) {
//            Set<byte[]> coinAddressSet = coinData.getAddresses();
//            if (null != coinAddressSet) {
//                addresses.addAll(coinAddressSet);
//            }
//        }
//        if (txData != null) {
//            Set<byte[]> txAddressSet = txData.getAddresses();
//            if (null != txAddressSet) {
//                addresses.addAll(txAddressSet);
//            }
//        }
//        if(scriptSig != null) {
//            try {
//                P2PKHScriptSig sig = P2PKHScriptSig.createFromBytes(scriptSig);
//                byte[] address = AddressTool.getAddress(sig);
//
//                boolean hasExist = false;
//                for(byte[] as : addresses) {
//                    if(Arrays.equals(as, address)) {
//                        hasExist = true;
//                        break;
//                    }
//                }
//
//                if(!hasExist) {
//                    addresses.add(address);
//                }
//            } catch (NulsException e) {
//                Log.error(e);
//            }
//        }
//        return new ArrayList<>(addresses);
//    }

    public byte[] serializeForHash() throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            int size = size() - SerializeUtils.sizeOfBytes(scriptSig);

            bos = new UnsafeByteArrayOutputStream(size);
            NulsOutputStreamBuffer buffer = new NulsOutputStreamBuffer(bos);
            if (size == 0) {
                bos.write(Constant.PLACE_HOLDER);
            } else {
                buffer.writeVarInt(type);
                buffer.writeVarInt(time);
                buffer.writeBytesWithLength(remark);
                buffer.writeNulsData(txData);
                buffer.writeNulsData(coinData);
            }
            return bos.toByteArray();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }

    public abstract String getInfo(byte[] address);
}
