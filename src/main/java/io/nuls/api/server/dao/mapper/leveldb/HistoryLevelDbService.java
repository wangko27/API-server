package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.DBService;

/**
 * Description: 14天交易历史
 * Author: moon
 * Date:  2018/8/24 0024
 */
public class HistoryLevelDbService {
    private static HistoryLevelDbService instance;
    public static HistoryLevelDbService getInstance(){
        if(null == instance){
            instance = new HistoryLevelDbService();
        }
        return instance;
    }

    private DBService dbService = LevelDbUtil.getInstance();


    public int insert(String key,String[] history) {
        Result result = dbService.putModel(Constant.HISTORY_DB_NAME, key.getBytes(), history);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        Result result = dbService.delete(Constant.HISTORY_DB_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public String[] select(String key) {
        return dbService.getModel(Constant.HISTORY_DB_NAME, key.getBytes(), String[].class);
    }
}
