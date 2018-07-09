package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dao.util.EhcacheUtil;

import java.util.ArrayList;
import java.util.List;

public class UtxoContext {

    private static UtxoLevelDbService utxoLevelDbService = UtxoLevelDbService.getInstance();
    //根据地址，把List<hashIndex> 放入缓存
    public static void put(String address,String hashIndex){
        List<String> list = (List<String>) EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME,address);
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(hashIndex);
        EhcacheUtil.getInstance().put(Constant.UTXO_CACHE_NAME,address,list);
    }
    public static void remove(String address) {
        List<String> hashIndexList = get(address);
        if (hashIndexList != null) {
            hashIndexList.remove(address);
        }
        EhcacheUtil.getInstance().remove(Constant.UTXO_CACHE_NAME,address);
    }
    public static List<String> get(String address) {
        return (List<String>)EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME,address);
    }
    public static List<Utxo> getUtxoList(String address){
        List<String> hashIndexList = get(address);
        List<Utxo> list = new ArrayList<>();
        if(null != hashIndexList && hashIndexList.size() > 0){
            //去leveldb加载utxo
            //todo 这里可能需要缓存，之后根据效率再考虑
            for(String hashIndex: hashIndexList){
                Utxo utxo = utxoLevelDbService.select(hashIndex);
                if(null != utxo){
                    list.add(utxo);
                }
            }
        }
        return list;

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
