package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.model.Result;
import io.nuls.api.server.dto.UtxoDto;
import io.nuls.api.server.leveldb.service.DBService;

import java.util.List;

/**
 * Description: 14天交易历史
 * Author: zsj
 * Date:  2018/8/24 0024
 */
public class BalanceLevelDbService {
    private static BalanceLevelDbService instance;
    public static BalanceLevelDbService getInstance(){
        if(null == instance){
            instance = new BalanceLevelDbService();
        }
        return instance;
    }

    private DBService dbService = LevelDbUtil.getInstance();


    public int insert(String key,List<UtxoDto> history) {
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

    public List<UtxoDto> select(String key) {
        return dbService.getModel(Constant.HISTORY_DB_NAME, key.getBytes(), List.class);
    }
}
