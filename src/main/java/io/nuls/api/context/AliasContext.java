package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Alias;
import io.nuls.api.server.dao.util.EhcacheUtil;
import io.nuls.api.utils.StringUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import java.util.List;

/**
 * Description: 缓存别名
 * Author: zsj
 * Date:  2018/6/5 0005
 */
public class AliasContext {

    public static Alias get(String address) {
        return (Alias)EhcacheUtil.getInstance().get(Constant.ALIAS_CACHE_NAME,address);
    }
    public static int getSize(){
        return EhcacheUtil.getInstance().get(Constant.ALIAS_CACHE_NAME).getSize();
    }
    public static void put(Alias alias) {
        if(null != alias && StringUtils.isNotBlank(alias.getAddress())){
            EhcacheUtil.getInstance().put(Constant.ALIAS_CACHE_NAME,alias.getAddress(),alias);
        }
    }

    public static void remove(String address) {
        EhcacheUtil.getInstance().remove(Constant.ALIAS_CACHE_NAME,address);
    }

    /*private static Map<String, Alias> aliasMap = new ConcurrentHashMap<>();

    public static Alias get(String address) {
        return aliasMap.get(address);
    }
    public static int getSize(){
        return aliasMap.size();
    }
    public static void put(Alias alias) {
        if(null != alias && StringUtils.isNotBlank(alias.getAddress())){
            aliasMap.put(alias.getAddress(),alias);
        }
    }

    public static void remove(String address) {
        if(aliasMap.containsKey(address)){
            aliasMap.remove(address);
        }
    }

    public static void removeByHeight(Long height){
        Map<String, Alias> tempMap = aliasMap;
        for(Alias alias:tempMap.values()){
            if(alias.getBlockHeight()==height){
                remove(alias.getAddress());
            }
        }
    }
    */

}
