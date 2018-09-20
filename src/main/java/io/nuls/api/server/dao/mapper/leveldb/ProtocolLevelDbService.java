package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.crypto.Util;
import io.nuls.api.entity.Utxo;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.BatchOperation;
import io.nuls.api.server.leveldb.service.DBService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * Author: vivi
 * Date:  2018/7/8 0008
 */
public class ProtocolLevelDbService {

    private static ProtocolLevelDbService instance = new ProtocolLevelDbService();

    private ProtocolLevelDbService() {
    }

    public static ProtocolLevelDbService getInstance() {
        return instance;
    }

    private DBService dbService = LevelDbUtil.getInstance();

    public Result saveChangeTxHashBlockHeight(Long effectiveHeight) {
        return dbService.put(Constant.PROTOCOL_DB_NAME, Constant.CHANGE_HASH_HEIGHT_KEY, Util.longToBytes(effectiveHeight));

    }

    public Long getChangeTxHashBlockHeight() {
        byte[] height = dbService.get(Constant.PROTOCOL_DB_NAME, Constant.CHANGE_HASH_HEIGHT_KEY);
        return height == null ? null : Long.valueOf(Util.byteToInt(height));
    }
}
