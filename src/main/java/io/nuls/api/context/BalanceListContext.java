package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.server.dao.util.EhcacheUtil;
import io.nuls.api.server.dto.UtxoDto;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Description: 持币账户静态统计
 * Author: zsj
 * Date:  2018/6/5 0005
 */
public class BalanceListContext {


    public static UtxoDto get(String address) {
        return (UtxoDto) EhcacheUtil.getInstance().get(Constant.BALANCE_CACHE_NAME,address);
    }
    public static int getSize(){
        return EhcacheUtil.getInstance().get(Constant.BALANCE_CACHE_NAME).getSize();
    }
    public static void add(UtxoDto block){
        EhcacheUtil.getInstance().put(Constant.BALANCE_CACHE_NAME,block.getAddress(),block);
    }
    public static List<UtxoDto> getAll(){
        List<UtxoDto> list = new ArrayList<>();
        Cache cache = EhcacheUtil.getInstance().get(Constant.BALANCE_CACHE_NAME);
        List<String> keys = cache.getKeys();
        for (String key:keys){
            Element element = cache.get(key);
            UtxoDto utxoDto = (UtxoDto)element.getObjectValue();
            list.add(utxoDto);
        }
        //按照金额，排序
        Collections.sort(list, new Comparator<UtxoDto>() {
            @Override
            public int compare(UtxoDto o1, UtxoDto o2) {
                return o2.getTotal().compareTo(o1.getTotal());
            }
        });
        System.out.println(list.size());
        return list;
    }
    public static void remove(String address) {
        EhcacheUtil.getInstance().remove(Constant.BALANCE_CACHE_NAME,address);
    }
    public static void clear(){
        EhcacheUtil.getInstance().get(Constant.BALANCE_CACHE_NAME).removeAll();
    }
    public static void reset(List<UtxoDto> list){
        clear();
        for(UtxoDto dto: list){
            add(dto);
        }
    }

    /*private static List<UtxoDto> blockDtos = new ArrayList<>();
    public static void add(UtxoDto block){
        blockDtos.add(block);
    }

    public static List<UtxoDto> getAll(){
        return blockDtos;
    }

    public static Integer getSize(){
        return blockDtos.size();
    }

    public static void clear(){
        blockDtos.clear();
    }
    public static void reset(List<UtxoDto> list){
        clear();
        blockDtos = list;
    }*/
}
