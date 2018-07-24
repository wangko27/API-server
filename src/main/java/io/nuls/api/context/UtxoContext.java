package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.AddressHashIndex;
import io.nuls.api.server.dao.mapper.leveldb.AddressHashIndexLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dao.util.EhcacheUtil;

import java.util.*;

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
        //addressHashIndexLevelDbService.insert(address,list);
    }

    //根据hashIndex，移除某个utxo
    public static void remove(String address, String key) {
        Set<String> list = get(address);
        if (list != null) {
            list.remove(key);
            //重置leveldb
            //addressHashIndexLevelDbService.insert(address,list);
            addressHashIndexLevelDbService.insert(new AddressHashIndex(address, list));
        }
    }

    //根据地址，获取该地址所有的未花费的hashIndex
    public static Set<String> get(String address) {
        Set<String> setList = (Set<String>) EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME, address);
        if(null == setList){
            //setList = addressHashIndexLevelDbService.select(address);
            AddressHashIndex addressHashIndex = addressHashIndexLevelDbService.select(address);
            if(null != addressHashIndex){
                setList = addressHashIndex.getHashIndexSet();
                if(null == setList){
                    setList = new HashSet<>();
                }
            }else{
                setList = new HashSet<>();
            }
        }
        return setList;
    }

    //启动的时候，初始化缓存
    public static void initCache(List<AddressHashIndex> list){
        for(AddressHashIndex hashIndex: list){
            EhcacheUtil.getInstance().put(Constant.UTXO_CACHE_NAME, hashIndex.getAddress(), hashIndex.getHashIndexSet());
        }
    }


    //-----------------------------------------------------第二次修改
    /*public static void put(Utxo utxo){
        List<Utxo> list = (List<Utxo>) EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME,utxo.getAddress());
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(utxo);
        EhcacheUtil.getInstance().put(Constant.UTXO_CACHE_NAME,utxo.getAddress(),list);
    }
    public static void remove(String address) {
        EhcacheUtil.getInstance().remove(Constant.UTXO_CACHE_NAME,address);
    }
    public static List<Utxo> get(String address) {
        return (List<Utxo>)EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME,address);
    }*/
    //------------------------------------------------------第一次修改


    /*//存放所有地址的未花费输出
    private static Map<String, List<Utxo>> utxoMap = new ConcurrentHashMap<>();


    public static List<Utxo> get(String address) {
        return utxoMap.get(address);
    }
    public static int getSize(){
        return utxoMap.size();
    }
    public static void put(Utxo utxo) {
        List<Utxo> list = utxoMap.get(utxo.getAddress());
        if (list == null) {
            list = new ArrayList<>();
            utxoMap.put(utxo.getAddress(), list);
        }
        list.add(utxo);
    }

    public static void remove(Utxo utxo) {
        List<Utxo> list = utxoMap.get(utxo.getAddress());
        if (list != null) {
            list.remove(utxo);
        }
    }*/
}
