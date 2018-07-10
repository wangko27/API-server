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

            if(checkAreaName(Constant.UTXO_CACHE_NAME)){
                //utxo
                dbService.createArea(Constant.UTXO_CACHE_NAME);
            }

            if(checkAreaName(Constant.TRANSACTION_CACHE_NAME)) {
                //transaction
                dbService.createArea(Constant.TRANSACTION_CACHE_NAME);
            }

            if(checkAreaName(Constant.BLOCKHEADER_CACHE_NAME)) {
                //blockheader
                dbService.createArea(Constant.BLOCKHEADER_CACHE_NAME);
            }

        }
        return dbService;
    }
    private static boolean checkAreaName(String name){
        String[] areaList = dbService.listArea();
        for(int i=0;i<areaList.length;i++){
            if(areaList[i].equals(name)){
                return false;
            }
        }
        return true;
    }
}
