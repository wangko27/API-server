package io.nuls.api.entity;

/**
 * @author: Charlie
 * @date: 2018/4/2
 */
public class RpcClientSearchResult extends RpcClientResult {

    private byte type;

    public RpcClientSearchResult(RpcClientResult rcr, byte type){
        super(rcr.isSuccess(), rcr.getCode(), rcr.getMsg(), rcr.getData());
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
