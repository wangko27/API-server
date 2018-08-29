package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Alias;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.DBService;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 别名leveldb，暂时不使用
 * Author: zsj
 * Date:  2018/8/24 0024
 */
public class AliasLevelDbService {
    private static AliasLevelDbService instance;
    public static AliasLevelDbService getInstance(){
        if(null == instance){
            instance = new AliasLevelDbService();
        }
        return instance;
    }

    private DBService dbService = LevelDbUtil.getInstance();


    public int insert(Alias alias) {
        Result result = dbService.putModel(Constant.UTXO_DB_ALIAS_NAME, alias.getAddress().getBytes(), alias);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        Result result = dbService.delete(Constant.UTXO_DB_ALIAS_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public Alias select(String key) {
        return dbService.getModel(Constant.UTXO_DB_ALIAS_NAME, key.getBytes(), Alias.class);
    }

    public List<Alias> getAll(){
        List<Alias> allList = dbService.values(Constant.UTXO_DB_ALIAS_NAME,Alias.class);
        if(allList == null){
            allList = new ArrayList<>();
        }
        return allList;
    }
}
