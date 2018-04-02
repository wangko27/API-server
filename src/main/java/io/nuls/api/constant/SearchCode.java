package io.nuls.api.constant;

/**
 * @author: lichao
 * @date: 2018/4/2
 */
public enum SearchCode {

    TX_HASH("tx_hash", (byte)1),
    ACCOUNT_ADDRESS("account_address", (byte)2),
    HEADER_HASH("header_hash", (byte)3),
    HEADER_HEIGHT("header_height", (byte)4);

    private final String msg;
    private final byte code;


    SearchCode(String msg, byte code){
        this.msg = msg;
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
