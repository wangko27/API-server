package io.nuls.api.model;

import java.util.Set;

/**
 * author Facjas
 * date 2018/5/10.
 */
public abstract class TransactionLogicData extends BaseNulsData {
    public abstract Set<byte[]> getAddresses();
}
