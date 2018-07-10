package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Utxo;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.BatchOperation;
import io.nuls.api.server.leveldb.service.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/8 0008
 */
@Service
public class UtxoLevelDbService {
    @Autowired
    private DBService dbService;

    public void insertList(List<Utxo> list) {
        BatchOperation batch = dbService.createWriteBatch(Constant.UTXO_DB_NAME);
        for (Utxo utxo : list) {
            batch.putModel(utxo.getKey().getBytes(), utxo);
        }
        batch.executeBatch();
    }

    public void insertMap(Map<String, Utxo> utxoMap) {
        BatchOperation batch = dbService.createWriteBatch(Constant.UTXO_DB_NAME);
        for (Utxo utxo : utxoMap.values()) {
            batch.putModel(utxo.getKey().getBytes(), utxo);
        }
        batch.executeBatch();
    }

    public int insert(Utxo utxo) {
        Result<Utxo> result = dbService.putModel(Constant.UTXO_DB_NAME, utxo.getKey().getBytes(), utxo);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        Result result = dbService.delete(Constant.UTXO_DB_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public Utxo select(String key) {
        return dbService.getModel(Constant.UTXO_DB_NAME, key.getBytes(), Utxo.class);
    }

    public List<Utxo> selectList(List<String> keyList) {
        List<Utxo> utxoList = new ArrayList<>();
        if (null != keyList && !keyList.isEmpty()) {
            for (String key : keyList) {
                Utxo utxo = select(key);
                if (null != utxo) {
                    utxoList.add(utxo);
                }
            }
        }
        return utxoList;
    }

    //这里会查询出leveldb里面全部的数据，谨慎使用
    public List<Utxo> getList() {
        return dbService.values(Constant.UTXO_DB_NAME, Utxo.class);
    }
}
