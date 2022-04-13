package com.wuxin.blog.shiro.redisCache;

import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * https://github.com/alexxiyang/shiro-redis/tree/master/src/main/java/org/crazycake/shiro
 * @Author: wuxin001
 * @Date: 2022/01/11/21:04
 * @Description: shiro集成redis
 */
public class RedisShiroCacheManager implements CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisShiroCacheManager.class);
    private final ConcurrentMap<String, MyRedisCache> caches = new ConcurrentHashMap();

    @Override
    public <K, V> MyRedisCache<K,V> getCache(String name) throws CacheException {
        logger.info("MyRedisCache name:{}",name);
        MyRedisCache cache = this.caches.get(name);
        if (cache == null) {
            cache = new MyRedisCache(name);
            this.caches.put(name, cache);
        }
        return cache;
    }

}
