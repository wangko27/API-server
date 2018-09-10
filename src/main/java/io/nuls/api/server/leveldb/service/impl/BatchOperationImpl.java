/**
 * MIT License
 * Copyright (c) 2017-2018 nuls.io
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.api.server.leveldb.service.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.server.leveldb.manager.LevelDBManager;
import io.nuls.api.server.leveldb.service.BatchOperation;
import io.nuls.api.model.Result;
import io.nuls.api.utils.log.Log;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.io.IOException;

public class BatchOperationImpl implements BatchOperation {

    private static final Result FAILED_NULL = Result.getFailed(KernelErrorCode.NULL_PARAMETER);
    private static final Result SUCCESS = Result.getSuccess();
    private static final Result FAILED_BATCH_CLOSE = Result.getFailed(KernelErrorCode.DB_BATCH_CLOSE);
    private String area;
    private DB db;
    private WriteBatch batch;
    private volatile boolean isClose = false;

    BatchOperationImpl(String area) {
        this.area = area;
        db = LevelDBManager.getArea(area);
        if(db != null) {
            batch = db.createWriteBatch();
        }
    }

    public Result checkBatch() {
        if(db == null) {
            return Result.getFailed(KernelErrorCode.DB_AREA_NOT_EXIST);
        }
        if(batch == null) {
            return Result.getFailed(KernelErrorCode.DB_UNKOWN_EXCEPTION);
        }
        return SUCCESS;
    }

    @Override
    public Result put(byte[] key, byte[] value) {
        if(key == null || value == null) {
            return FAILED_NULL;
        }
        batch.put(key, value);
        return SUCCESS;
    }

    @Override
    public <T> Result putModel(byte[] key, T value) {
        if(key == null || value == null) {
            return FAILED_NULL;
        }
        byte[] bytes = LevelDBManager.getModelSerialize(value);
        return put(key, bytes);
    }

    @Override
    public Result delete(byte[] key) {
        if(key == null) {
            return FAILED_NULL;
        }
        batch.delete(key);
        return SUCCESS;
    }

    private void close() {
        this.isClose = true;
    }

    private boolean checkClose() {
        return isClose;
    }

    @Override
    public Result executeBatch() {
        // 检查逻辑关闭
        if(checkClose()) {
            return FAILED_BATCH_CLOSE;
        }
        try {
            db.write(batch);
        } catch (Exception e) {
            Log.error(e);
            return Result.getFailed(KernelErrorCode.DB_UNKOWN_EXCEPTION);
        } finally {
            // Make sure you close the batch to avoid resource leaks.
            // 貌似LevelDB未实现此close方法, 所以加入一个逻辑关闭
            if(batch != null) {
                try {
                    this.close();
                    batch.close();
                } catch (IOException e) {
                    // skip it
                }
            }
        }
        return SUCCESS;
    }
}
