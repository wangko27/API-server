package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.AddressHashIndex;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.BatchOperation;
import io.nuls.api.server.leveldb.service.DBService;
import io.nuls.api.utils.log.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/8 0008
 */
public class AddressHashIndexLevelDbService {

    private static AddressHashIndexLevelDbService instance;
    public static AddressHashIndexLevelDbService getInstance(){
        if(null == instance){
            instance = new AddressHashIndexLevelDbService();
        }
        return instance;
    }

    private DBService dbService = LevelDbUtil.getInstance();

    /*public void insertList(List<AddressHashIndex> list) {
        BatchOperation batch = dbService.createWriteBatch(Constant.UTXO_DB_NAME);
        for (AddressHashIndex addressHashIndex : list) {
            batch.putModel(addressHashIndex.getAddress().getBytes(), addressHashIndex);
        }
        batch.executeBatch();
    }*/

    public int insert(String key, Set<String> value) {
        Result result = dbService.putModel(Constant.UTXO_DB_ADDRESS_NAME, key.getBytes(), value);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        Result result = dbService.delete(Constant.UTXO_DB_ADDRESS_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public Set<String> select(String key) {
        return dbService.getModel(Constant.UTXO_DB_ADDRESS_NAME, key.getBytes(), Set.class);
    }

    //查询所有address
    public Set<String> getList() {
        Set<String> addressList = new HashSet<>();
        Set<byte[]> setList =dbService.keySet(Constant.UTXO_DB_ADDRESS_NAME);
        for(byte[] setAddr : setList){
            try {
                addressList.add(new String(setAddr,"utf-8"));
            } catch (UnsupportedEncodingException e) {
                Log.error("获取addre异常了");
            }
        }
        return addressList;
    }

    //查询所有的值
    public List<AddressHashIndex> getAll(){
        List<AddressHashIndex> allList = dbService.values(Constant.UTXO_DB_ADDRESS_NAME,AddressHashIndex.class);
        if(allList == null){
            allList = new ArrayList<>();
        }
        return allList;
    }
}
