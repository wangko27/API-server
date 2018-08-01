package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Utxo;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.DBService;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 未确认交易的utxo
 * Author: zsj
 * Date:  2018/7/8 0008
 */
public class WebwalletUtxoLevelDbService {

    private static WebwalletUtxoLevelDbService instance;
    public static WebwalletUtxoLevelDbService getInstance(){
        if(null == instance){
            instance = new WebwalletUtxoLevelDbService();
        }
        return instance;
    }

    private DBService dbService = LevelDbUtil.getInstance();

    public int insert(Utxo utxo) {
        Result result = dbService.putModel(Constant.WEBWALLETUTXO_DB_NAME, utxo.getAddress().getBytes(), utxo);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        Result result = dbService.delete(Constant.WEBWALLETUTXO_DB_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public void deleteAll(){
        dbService.clearArea(Constant.WEBWALLETUTXO_DB_NAME);
    }

    public Utxo select(String key) {
        return dbService.getModel(Constant.WEBWALLETUTXO_DB_NAME, key.getBytes(), Utxo.class);
    }

    //查询所有的值
    public List<Utxo> getAll(){
        List<Utxo> allList = dbService.values(Constant.WEBWALLETUTXO_DB_NAME,Utxo.class);
        if(allList == null){
            allList = new ArrayList<>();
        }
        return allList;
    }
}
