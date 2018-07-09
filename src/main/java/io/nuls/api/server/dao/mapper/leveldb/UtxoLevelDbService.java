package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Utxo;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.BatchOperation;
import io.nuls.api.server.leveldb.service.DBService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/8 0008
 */
public class UtxoLevelDbService {

    private DBService dbService = LevelDbUtil.getInstance();

    private static UtxoLevelDbService utxoLevelDbService;

    public static UtxoLevelDbService getInstance() {
        if (null == utxoLevelDbService) {
            utxoLevelDbService = new UtxoLevelDbService();
        }
        return utxoLevelDbService;
    }

    public void insertList(List<Utxo> list) {
        BatchOperation batch = dbService.createWriteBatch(Constant.UTXO_CACHE_NAME);
        for (Utxo utxo : list) {
            batch.putModel(utxo.getKey().getBytes(), utxo);
        }
        batch.executeBatch();
    }

    public void insertMap(Map<String, Utxo> utxoMap) {
        BatchOperation batch = dbService.createWriteBatch(Constant.UTXO_CACHE_NAME);
        for (Utxo utxo : utxoMap.values()) {
            batch.putModel(utxo.getKey().getBytes(), utxo);
        }
        batch.executeBatch();
    }

    public int insert(Utxo utxo) {
        Result<Utxo> result = dbService.putModel(Constant.UTXO_CACHE_NAME, utxo.getKey().getBytes(), utxo);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        DBService dbService = LevelDbUtil.getInstance();
        Result result = dbService.delete(Constant.UTXO_CACHE_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public Utxo select(String key) {
        DBService dbService = LevelDbUtil.getInstance();
        return dbService.getModel(Constant.UTXO_CACHE_NAME, key.getBytes(), Utxo.class);
    }

    //这里会查询出leveldb里面全部的数据，谨慎使用
    public List<Utxo> getList() {
        return dbService.values(Constant.UTXO_CACHE_NAME, Utxo.class);
    }
}
