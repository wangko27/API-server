package io.nuls.api.context;

import io.nuls.api.entity.Utxo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UtxoContext {

    //存放所有地址的未花费输出
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
    }
}
