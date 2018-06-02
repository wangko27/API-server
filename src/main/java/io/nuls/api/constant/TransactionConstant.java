package io.nuls.api.constant;

import io.nuls.api.model.Na;

public interface TransactionConstant {

    int TX_TYPE_COINBASE = 1;
    int TX_TYPE_TRANSFER = 2;
    int TX_TYPE_ACCOUNT_ALIAS = 51;
    int TX_TYPE_REGISTER_AGENT = 90;
    int TX_TYPE_JOIN_CONSENSUS = 91;
    int TX_TYPE_CANCEL_DEPOSIT = 92;
    int TX_TYPE_STOP_AGENT = 95;
    int TX_TYPE_YELLOW_PUNISH = 93;
    int TX_TYPE_RED_PUNISH = 94;

    int PUBLISH_YELLOW = 1;
    int PUTLISH_RED = 2;


    Na ALIAS_NA = Na.parseNuls(1);
}
