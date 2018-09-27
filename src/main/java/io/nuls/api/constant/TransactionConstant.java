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

package io.nuls.api.constant;

/**
 * Description: 各种交易用到的常量
 * Author: moon
 * Date:  2018/5/31 0031
 */
public class TransactionConstant {
    /**
     * KB
     */
    public static final int KB = 1024;
    /**
     * 交易最大容量
     */
    public static final int MAX_TX_SIZE = 300 * KB;
    /**
     * 备注最大容量
     */
    public final static int MAX_REMARK_LEN = 100;
    /**
     * from交易大小
     */
    public final static int BASE_FROM_SIZE = 50;
    /**
     * to交易大小
     */
    public final static int BASE_TO_SIZE = 38;
    /**
     * 交易基础大小
     */
    public final static int BASE_TRANSACTION_SIZE = 124;

}
