package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.util.EhcacheUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UtxoContext {

    public static void put(Utxo utxo){
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
    }
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
