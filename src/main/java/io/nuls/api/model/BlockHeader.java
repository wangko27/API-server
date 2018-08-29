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

import io.nuls.api.crypto.script.P2PKHScriptSig;
import io.nuls.api.exception.NulsException;
import io.nuls.api.exception.NulsRuntimeException;
import io.nuls.api.utils.AddressTool;
import io.nuls.api.utils.NulsByteBuffer;
import io.nuls.api.utils.NulsOutputStreamBuffer;
import io.nuls.api.utils.SerializeUtils;
import io.nuls.api.utils.log.Log;

import java.io.IOException;

/**
 * @author vivi
 * @date 2017/10/30
 */
public class BlockHeader extends BaseNulsData {

    private transient NulsDigestData hash;
    private NulsDigestData preHash;
    private NulsDigestData merkleHash;
    private long time;
    private long height;
    private long txCount;
    private P2PKHScriptSig scriptSign;
    private byte[] extend;

    private transient int size;
    private transient byte[] packingAddress;

    public BlockHeader() {
    }

    protected synchronized void calcHash() {
        if (null != this.hash) {
            return;
        }
        hash = forceCalcHash();
    }

    @Override
    public int size() {
        int size = 0;
        size += SerializeUtils.sizeOfNulsData(preHash);
        size += SerializeUtils.sizeOfNulsData(merkleHash);
        size += SerializeUtils.sizeOfUint48();
        size += SerializeUtils.sizeOfUint32();
        size += SerializeUtils.sizeOfUint32();
        size += SerializeUtils.sizeOfBytes(extend);
        size += SerializeUtils.sizeOfNulsData(scriptSign);
        return size;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeNulsData(preHash);
        stream.writeNulsData(merkleHash);
        stream.writeUint48(time);
        stream.writeUint32(height);
        stream.writeUint32(txCount);
        stream.writeBytesWithLength(extend);
        stream.writeNulsData(scriptSign);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.preHash = byteBuffer.readHash();
        this.merkleHash = byteBuffer.readHash();
        this.time = byteBuffer.readUint48();
        this.height = byteBuffer.readUint32();
        this.txCount = byteBuffer.readUint32();
        this.extend = byteBuffer.readByLengthByte();
        try {
            this.hash = NulsDigestData.calcDigestData(this.serialize());
        } catch (IOException e) {
            Log.error(e);
        }
        this.scriptSign = byteBuffer.readNulsData(new P2PKHScriptSig());
    }

    private NulsDigestData forceCalcHash() {
        try {
            BlockHeader header = (BlockHeader) this.clone();
            header.setScriptSig(null);
            return NulsDigestData.calcDigestData(header.serialize());
        } catch (Exception e) {
            throw new NulsRuntimeException(e);
        }
    }

    public NulsDigestData getHash() {
        if (null == hash) {
            calcHash();
        }
        return hash;
    }

    public void setHash(NulsDigestData hash) {
        this.hash = hash;
    }

    public NulsDigestData getPreHash() {
        return preHash;
    }

    public void setPreHash(NulsDigestData preHash) {
        this.preHash = preHash;
    }

    public NulsDigestData getMerkleHash() {
        return merkleHash;
    }

    public void setMerkleHash(NulsDigestData merkleHash) {
        this.merkleHash = merkleHash;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getTxCount() {
        return txCount;
    }

    public void setTxCount(long txCount) {
        this.txCount = txCount;
    }

    public P2PKHScriptSig getScriptSig() {
        return scriptSign;
    }

    public void setScriptSig(P2PKHScriptSig scriptSign) {
        this.scriptSign = scriptSign;
    }

    public byte[] getPackingAddress() {
        if (null == packingAddress && this.scriptSign != null) {
            this.packingAddress = AddressTool.getAddress(scriptSign.getPublicKey());
        }
        return packingAddress;
    }


    public byte[] getExtend() {
        return extend;
    }

    public void setExtend(byte[] extend) {
        this.extend = extend;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPackingAddress(byte[] packingAddress) {
        this.packingAddress = packingAddress;
    }
}
