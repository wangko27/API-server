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
package io.nuls.api.server.leveldb.service;

import io.nuls.api.model.Result;

public interface BatchOperation {
//
//    /**
//     * 增加或者更新操作
//     * Add or update operations.
//     *
//     * @param key
//     * @param value
//     * @return
//     */
    Result put(byte[] key, byte[] value);

//    /**
//     * 存储或者更新对象
//     * Add or update the object
//     *
//     * @param area
//     * @param key
//     * @param value 需要存储或者更新的对象/Objects that need to be added or updated.
//     * @return
//     */
    <T> Result putModel(byte[] key, T value);

//    /**
//     * 删除操作
//     * Delete operation
//     *
//     * @param key
//     * @return
//     */
    Result delete(byte[] key);

//    /**
//     * 执行批量操作
//     * Perform batch operation
//     *
//     * @return
//     */
    Result executeBatch();
}
