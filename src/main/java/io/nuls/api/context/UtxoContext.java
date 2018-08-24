package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.AddressHashIndex;
import io.nuls.api.server.dao.mapper.leveldb.AddressHashIndexLevelDbService;
import io.nuls.api.server.dao.util.EhcacheUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * utxo 缓存管理
 */
public class UtxoContext {

    private static AddressHashIndexLevelDbService addressHashIndexLevelDbService = AddressHashIndexLevelDbService.getInstance();

    //根据地址，把List<key> 放入缓存
    public static void put(String address, String key) {
        Set<String> list = get(address);
        list.add(key);
        //重置缓存
        EhcacheUtil.getInstance().put(Constant.UTXO_CACHE_NAME, address, list);
        //重置leveldb
        addressHashIndexLevelDbService.insert(new AddressHashIndex(address, list));
    }

    public static void put(String address, Set<String> list){
        //重置缓存
        EhcacheUtil.getInstance().put(Constant.UTXO_CACHE_NAME, address, list);
        //重置leveldb
        addressHashIndexLevelDbService.insert(new AddressHashIndex(address, list));
    }

    public static void putMap(Map<String,AddressHashIndex> attrMapList){
        for(AddressHashIndex addressHashIndex:attrMapList.values()){
            //重置缓存
            EhcacheUtil.getInstance().put(Constant.UTXO_CACHE_NAME, addressHashIndex.getAddress(), addressHashIndex.getHashIndexSet());
            //重置leveldb
            addressHashIndexLevelDbService.insert(addressHashIndex);
        }
    }

    //根据地址，获取该地址所有的未花费的hashIndex
    public static Set<String> get(String address) {
        Set<String> setList = (Set<String>) EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME, address);
        if(null == setList){
            AddressHashIndex addressHashIndex = addressHashIndexLevelDbService.select(address);
            if(null != addressHashIndex){
                setList = addressHashIndex.getHashIndexSet();
                if(null == setList){
                    setList = new HashSet<>();
                }
            }else{
                setList = new HashSet<>();
            }
            //如果不存在，则直接重新放入缓存
            put(address,setList);
        }
        return setList;
    }

    //启动的时候，初始化缓存
    public static void initCache(List<AddressHashIndex> list){
        for(AddressHashIndex hashIndex: list){
            EhcacheUtil.getInstance().put(Constant.UTXO_CACHE_NAME, hashIndex.getAddress(), hashIndex.getHashIndexSet());
        }
    }

}
