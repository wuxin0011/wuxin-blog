package com.wuxin.blog.shiro.redisCache;

import com.wuxin.blog.pojo.User;
import com.wuxin.blog.redis.RedisKey;
import com.wuxin.blog.redis.RedisService;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Set;

/**
 * 使用redis-cache实现权限控制
 *
 * @Author: wuxin001
 * @Date: 2022/01/12/0:25
 * @Description: redis cache
 */
@Component
public class MyRedisCache<K, V> implements Cache<K, V> {

    private String cacheName;


    public MyRedisCache() {

    }

    public MyRedisCache(String cacheName) {
        this.cacheName = cacheName;
    }


    @Autowired
    private RedisService service;

    private static RedisService redisService;


    private static final String USER_ROLES = RedisKey.USER_ROLES;

    private static final String USER_KEY = "roles_cache_key";

    private static final Long EXPIRE = RedisKey.EXPIRE;

    private static final Logger logger = LoggerFactory.getLogger(MyRedisCache.class);

    /**
     * redisService = null 问题 这里因为Autowired 和 Component加载顺序问题！
     */
    @PostConstruct
    public void getRedisService() {
        redisService = this.service;
    }


    @Override
    public V get(K key) throws CacheException {
        User user = getUser(key);
        V value = getValue(user);
        logger.info(" 从缓存容器:{}中获取用户权限信息=====>value:{}",this.cacheName,value);
        return value;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        User user = getUser(key);
        logger.info("添加用户权限信息到缓存容器:{}:{}",this.cacheName, user);
        setValue(user, value);
        return value;
    }

    @Override
    public V remove(K k) throws CacheException {
        User user = getUser(k);
        if (user != null) {
            String hk = RedisKey.getKey(user.getUserId(), USER_KEY);
            redisService.hdel(USER_ROLES, hk);
        }
        logger.info("用户退出移除了用户缓存信息");
        return getValue(user);
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }


    public User getUser(K key) {
        User user = null;
        if (key instanceof SimplePrincipalCollection) {
            SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) key;
            Object object = principalCollection.getPrimaryPrincipal();
            if (object instanceof User) {
                user = (User) object;
            }
        }
        return user;
    }

    public void setValue(User user, V value) {
        if (user != null) {
            String hk = RedisKey.getKey(user.getUserId(), USER_KEY);
            boolean hasKey = redisService.hHasKey(USER_ROLES, hk);
            if (!hasKey) {
                redisService.hset(USER_ROLES, hk, value, EXPIRE);
            }
        }

    }


    public V getValue(User user) {
        if (user != null) {
            String hk = RedisKey.getKey(user.getUserId(), USER_KEY);
            boolean hasKey = redisService.hHasKey(USER_ROLES, hk);
            if (hasKey) {
                Object o = redisService.hget(USER_ROLES, hk);
                if (o != null) {
                    return (V) o;
                }
            }
        }
        return null;
    }


}
