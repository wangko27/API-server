package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.server.leveldb.service.DBService;

/**
 * Description: levelDb Instance获取
 * Author: zsj
 * Date:  2018/7/7 0007
 */
public class LevelDbUtil {
    private static DBService dbService;

    public static DBService getInstance(){
        if(null == dbService){

        }
        return dbService;
    }
}
