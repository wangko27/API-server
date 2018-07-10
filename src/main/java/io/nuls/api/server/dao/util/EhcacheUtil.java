package io.nuls.api.server.dao.util;

import java.net.URL;

import io.nuls.api.constant.Constant;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/2 0002
 */
public class EhcacheUtil {

    private static final String path = "/ehcache.xml";

    private URL url;

    private CacheManager manager;

    private static EhcacheUtil ehCache;

    private EhcacheUtil(String path) {
        url = getClass().getResource(path);
        manager = CacheManager.create(url);
        //初始化缓存
        manager.addCache(Constant.UTXO_CACHE_NAME);
        manager.addCache(Constant.ALIAS_CACHE_NAME);
        manager.addCache(Constant.BALANCE_CACHE_NAME);
        manager.addCache(Constant.HISTORY_CACHE_NAME);
        manager.addCache(Constant.PACKINGADDRESS_CACHE_NAME);
        manager.addCache(Constant.ADDRESS_REWARD_DETAIL);

    }
    //得到缓存实例
    public static EhcacheUtil getInstance() {
        if (ehCache == null) {
            ehCache = new EhcacheUtil(path);
        }
        return ehCache;
    }
    //向缓存放入要存储的内容
    public void put(String cacheName, String key, Object value) {
        Cache cache = manager.getCache(cacheName);
        Element element = new Element(key, value);
        cache.put(element);
    }

    //向缓存放入要存储的内容
    public void putWithTime(String cacheName, String key, Object value,int seconds) {
        Cache cache = manager.getCache(cacheName);
        Element element = new Element(key, value);
        element.setTimeToLive(seconds);
        cache.put(element);
    }

    //向缓存取出要存储的内容
    public Object get(String cacheName, String key) {
        Cache cache = manager.getCache(cacheName);
        Element element = cache.get(key);

        return element == null ? null : element.getObjectValue();
    }
    //得到参数名称对应的缓存
    public Cache get(String cacheName) {
        return manager.getCache(cacheName);
    }
    //移除参数名称对应的缓存
    public void remove(String cacheName, String key) {
        Cache cache = manager.getCache(cacheName);
        cache.remove(key);
    }
}
