package io.nuls.api.model.tx;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.Alias;
import io.nuls.api.model.Transaction;
import io.nuls.api.utils.NulsByteBuffer;

/**
 * @author: Charlie
 * @date: 2018/5/11
 */
public class AliasTransaction extends Transaction<Alias> {

    public AliasTransaction() {
        super(EntityConstant.TX_TYPE_ACCOUNT_ALIAS);
    }

    protected AliasTransaction(int type) {
        super(type);
    }

    @Override
    public String getInfo(byte[] address) {
        return "-" + EntityConstant.ALIAS_NA.toCoinString();
    }

    @Override
    protected Alias parseTxData(NulsByteBuffer byteBuffer) throws NulsException {
        return byteBuffer.readNulsData(new Alias());
    }

}
