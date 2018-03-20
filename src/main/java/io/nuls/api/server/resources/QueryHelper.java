package io.nuls.api.server.resources;

import java.util.concurrent.ConcurrentHashMap;

public enum QueryHelper {
    HELPER;

    private ConcurrentHashMap<String, Long> pageMaxIdMap = new ConcurrentHashMap<>();

    public void putCache(String key, Long id) {
        pageMaxIdMap.put(key, id);
    }

    public Long getCache(String key) {
        return pageMaxIdMap.get(key);
    }

    public void clear() {
        pageMaxIdMap.clear();
    }
}
