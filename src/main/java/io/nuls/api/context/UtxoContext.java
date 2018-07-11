package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.server.dao.util.EhcacheUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtxoContext {

    //根据地址，把List<key> 放入缓存
    public static void put(String address, String key) {
        Set<String> list = (Set<String>) EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME, address);
        if (list == null) {
            list = new HashSet<>();
        }
        list.add(key);
        //重置缓存
        EhcacheUtil.getInstance().put(Constant.UTXO_CACHE_NAME, address, list);
    }

    //根据hashIndex，移除某个utxo
    public static void remove(String address, String key) {
        Set<String> list = get(address);
        if (list != null) {
            list.remove(key);
        }
    }

    public static Set<String> get(String address) {
        return (Set<String>) EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME, address);
    }

//    public static List<Utxo> getUtxoList(String address) {
//        List<String> keyList = get(address);
//        List<Utxo> utxoList = new ArrayList<>();
//        if (null != keyList && !keyList.isEmpty()) {
//            //去leveldb加载utxo
//            //todo 这里可能需要缓存，之后根据效率再考虑
//            for (String key : keyList) {
//                Utxo utxo = utxoLevelDbService.select(key);
//                if (null != utxo) {
//                    utxoList.add(utxo);
//                }
//            }
//        }
//        return utxoList;
//    }


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
