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
            dbService= new LevelDBServiceImpl();
            dbService.createArea(Constant.UTXO_DB_NAME);
            dbService.createArea(Constant.TRANSACTION_DB_NAME);
            dbService.createArea(Constant.BLOCKHEADER_DB_NAME);
            dbService.createArea(Constant.UTXO_DB_ADDRESS_NAME);
            dbService.createArea(Constant.WEBWALLETUTXO_DB_NAME);
            dbService.createArea(Constant.WEBWALLETTRANSACTION_DB_NAME);
            dbService.createArea(Constant.UTXO_DB_ALIAS_NAME);
            dbService.createArea(Constant.HISTORY_DB_NAME);
            dbService.createArea(Constant.BALANCE_DB_NAME);
            /*System.out.println("-------------休眠开始，20s，让leveldb加载数据");
            try {
                Thread.sleep(20000L);
            } catch (InterruptedException e) {
            }
            System.out.println("-------------休眠结束");*/
        }
        return dbService;
    }
}
