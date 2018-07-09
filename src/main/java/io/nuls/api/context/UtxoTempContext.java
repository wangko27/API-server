package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.util.EhcacheUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 临时使用，等input里面返回address之后就可以代替
 */
public class UtxoTempContext {

    /*public static void put(Utxo utxo){
        EhcacheUtil.getInstance().put(Constant.UTXO_TEMP_CACHE_NAME,utxo.getHashIndex(),utxo);
    }
    public static void remove(String hashIndex) {
        EhcacheUtil.getInstance().remove(Constant.UTXO_TEMP_CACHE_NAME,hashIndex);
    }
    public static Utxo get(String hashAndIndex) {
        return (Utxo)EhcacheUtil.getInstance().get(Constant.UTXO_TEMP_CACHE_NAME,hashAndIndex);
    }
    public static int getSize() {
        return EhcacheUtil.getInstance().get(Constant.UTXO_TEMP_CACHE_NAME).getSize();
    }

    *//**
     * 需要删除的utxo的hahs+index的队列，每隔一个小时清除已经花费的utxo
     *//*
    private static Queue<String> queue = new LinkedList<String>();

    *//**
     * 获取并删除第一个
     * @return
     *//*
    public static String getQueue(){
        return queue.poll();
    }

    *//**
     * 添加到最末
     * @param str
     * @return
     *//*
    public static boolean addQueue(String str){
        return queue.offer(str);
    }*/

}
