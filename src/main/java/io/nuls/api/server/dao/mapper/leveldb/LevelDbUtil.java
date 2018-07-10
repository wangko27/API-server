package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.DBService;
import io.nuls.api.server.leveldb.service.impl.LevelDBServiceImpl;

/**
 * Description: levelDb Instance获取
 * Author: zsj
 * Date:  2018/7/7 0007
 */
public class LevelDbUtil {
    private static DBService dbService;

    public static DBService getInstance(){
        if(null == dbService){
            dbService = new LevelDBServiceImpl();
            //utxo
            dbService.createArea(Constant.UTXO_CACHE_NAME);
            //transaction
            dbService.createArea(Constant.TRANSACTION_CACHE_NAME);
            //blockheader
            dbService.createArea(Constant.BLOCKHEADER_CACHE_NAME);
            System.out.println("-------------休眠开始，一分钟，让leveldb加载数据");
            try {
                Thread.sleep(600L);
            } catch (InterruptedException e) {
            }
            System.out.println("-------------休眠结束");
        }
        return dbService;
    }
}
