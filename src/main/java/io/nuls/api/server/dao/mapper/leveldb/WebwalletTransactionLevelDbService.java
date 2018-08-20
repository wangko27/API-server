package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.WebwalletTransaction;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.DBService;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 未确认交易
 * Author: zsj
 * Date:  2018/7/8 0008
 */
public class WebwalletTransactionLevelDbService {

    private static WebwalletTransactionLevelDbService instance;
    public static WebwalletTransactionLevelDbService getInstance(){
        if(null == instance){
            instance = new WebwalletTransactionLevelDbService();
        }
        return instance;
    }

    private DBService dbService = LevelDbUtil.getInstance();


    public int insert(WebwalletTransaction webwalletUtxo) {
        Result result = dbService.putModel(Constant.WEBWALLETTRANSACTION_DB_NAME, webwalletUtxo.getHash().getBytes(), webwalletUtxo);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        Result result = dbService.delete(Constant.WEBWALLETTRANSACTION_DB_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public WebwalletTransaction select(String key) {
        return dbService.getModel(Constant.WEBWALLETTRANSACTION_DB_NAME, key.getBytes(), WebwalletTransaction.class);
    }

    //查询所有的值
    public List<WebwalletTransaction> getAll(){
        List<WebwalletTransaction> allList = dbService.values(Constant.WEBWALLETTRANSACTION_DB_NAME,WebwalletTransaction.class);
        if(allList == null){
            allList = new ArrayList<>();
        }
        return allList;
    }
}
