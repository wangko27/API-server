package io.nuls.api.utils;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.constant.ErrorCode;
import io.nuls.api.exception.NulsRuntimeException;
import io.nuls.api.model.Transaction;
import io.nuls.api.model.tx.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionManager {

    private static final Map<Integer, Class<? extends Transaction>> TYPE_TX_MAP = new HashMap<>();

    static {

        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_COINBASE, CoinBaseTransaction.class);
        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_TRANSFER, TransferTransaction.class);
        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_ACCOUNT_ALIAS, AliasTransaction.class);
        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_REGISTER_AGENT, CreateAgentTransaction.class);
        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_JOIN_CONSENSUS, DepositTransaction.class);
        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_CANCEL_DEPOSIT, CancelDepositTransaction.class);
        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_STOP_AGENT, StopAgentTransaction.class);
        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_YELLOW_PUNISH, YellowPunishTransaction.class);
        TYPE_TX_MAP.put(EntityConstant.TX_TYPE_RED_PUNISH, RedPunishTransaction.class);
    }


    public static Transaction getInstance(NulsByteBuffer byteBuffer) throws Exception {
        int txType = (int) new NulsByteBuffer(byteBuffer.getPayloadByCursor()).readVarInt();
        Class<? extends Transaction> txClass = TYPE_TX_MAP.get(txType);
        if (null == txClass) {
            throw new NulsRuntimeException(ErrorCode.FAILED, "transaction type not exist!");
        }
        Transaction tx = byteBuffer.readNulsData(txClass.newInstance());
        return tx;
    }

    public static List<Transaction> getInstances(NulsByteBuffer byteBuffer, long txCount) throws Exception {
        List<Transaction> list = new ArrayList<>();
        for (int i = 0; i < txCount; i++) {
            list.add(getInstance(byteBuffer));
        }
        return list;
    }
}
