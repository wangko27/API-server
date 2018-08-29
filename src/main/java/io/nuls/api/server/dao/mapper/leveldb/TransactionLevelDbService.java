package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.Utxo;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.BatchOperation;
import io.nuls.api.server.leveldb.service.DBService;

import java.util.List;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/8 0008
 */
public class TransactionLevelDbService {

    private static TransactionLevelDbService instance;
    public static TransactionLevelDbService getInstance(){
        if(null == instance){
            instance = new TransactionLevelDbService();
        }
        return instance;
    }

    private DBService dbService = LevelDbUtil.getInstance();

    public void insertList(List<Transaction> list) {
        BatchOperation batch = dbService.createWriteBatch(Constant.TRANSACTION_DB_NAME);
        for (Transaction tx : list) {
            batch.putModel(tx.getHash().getBytes(), tx);
        }
        batch.executeBatch();
    }

    public int insert(Transaction tx) {
        Result<Utxo> result = dbService.putModel(Constant.TRANSACTION_DB_NAME, tx.getHash().getBytes(), tx);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        Result result = dbService.delete(Constant.TRANSACTION_DB_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public Transaction select(String key) {
        return dbService.getModel(Constant.TRANSACTION_DB_NAME, key.getBytes(), Transaction.class);
    }

    //这里会查询出leveldb里面全部的数据，谨慎使用
    public List<Transaction> getList() {
        return dbService.values(Constant.TRANSACTION_DB_NAME, Transaction.class);
    }
}
