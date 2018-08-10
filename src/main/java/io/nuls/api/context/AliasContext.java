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
        EhcacheUtil.getInstance().put(Constant.ALIAS_CACHE_NAME,alias.getAddress(),alias);
    }
    public static void putList(List<Alias> aliasList) {
        for(Alias alias: aliasList){
            EhcacheUtil.getInstance().put(Constant.ALIAS_CACHE_NAME,alias.getAddress(),alias);
        }
    }

    public static void remove(String address) {
        EhcacheUtil.getInstance().remove(Constant.ALIAS_CACHE_NAME,address);
    }
}
