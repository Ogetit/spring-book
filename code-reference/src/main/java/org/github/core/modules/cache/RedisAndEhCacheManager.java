package org.github.core.modules.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;

/**
 * Created by github on 2017/4/19.
 */
public class RedisAndEhCacheManager implements CacheManager {
    private Logger logger = LoggerFactory.getLogger(RedisAndEhCacheManager.class);
    @Autowired
    private EhCacheCacheManager ehcacheManagerSpring;
    @Autowired
    private RedisCacheManager redisManagerSpring;
    @Autowired
    private RedisTemplate redisTemplate;
    private boolean redisIsClose;


    @Override
    public Cache getCache(String name) {
        if (!redisIsClose) {
            try{
                redisIsClose = redisTemplate.getConnectionFactory().getConnection().isClosed();
            }catch (Exception e){
                redisIsClose = true;
                logger.error("redis连接失败", e);
            }
        }
        if (redisIsClose) {
            return ehcacheManagerSpring.getCache(name);
        } else {
            return redisManagerSpring.getCache(name);
        }
    }

    @Override
    public Collection<String> getCacheNames() {
        if (redisIsClose) {
            return ehcacheManagerSpring.getCacheNames();
        } else {
            return redisManagerSpring.getCacheNames();
        }
    }
}
