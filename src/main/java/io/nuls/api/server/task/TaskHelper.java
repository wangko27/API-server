package io.nuls.api.server.task;

import io.nuls.api.counter.QueryCounter;
import io.nuls.api.server.resources.QueryHelper;
import io.nuls.api.utils.log.Log;

public enum TaskHelper {
    HELPER;

    public void queryReset(BalanceTask balanceTask) {
        try {
            QueryCounter.setBalanceModify(1);
            QueryHelper.HELPER.clear();
        } catch (Exception e) {
            // skip
            Log.warn("query reset failed.", e);
        }
    }

    public void queryReset(MinedTask minedTask) {
        try {
            QueryCounter.setMinedModify(1);
            QueryHelper.HELPER.clear();
        } catch (Exception e) {
            // skip
            Log.warn("query reset failed.", e);
        }
    }
}
