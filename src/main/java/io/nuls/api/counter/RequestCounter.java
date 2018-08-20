package io.nuls.api.counter;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.nuls.api.utils.log.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestCounter {
    private static LoadingCache<String, AtomicInteger> IP_COUNTER_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(8)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .initialCapacity(10)
            .maximumSize(100)
            .build(
                    new CacheLoader<String, AtomicInteger>() {
                        @Override
                        public AtomicInteger load(String key) {
                            return new AtomicInteger(0);
                        }
                    }
            );

    public static int increment(String ip) throws ExecutionException {
        return IP_COUNTER_CACHE.get(ip).incrementAndGet();
    }

    public static int decrement(String ip) throws ExecutionException {
        return IP_COUNTER_CACHE.get(ip).decrementAndGet();
    }
}