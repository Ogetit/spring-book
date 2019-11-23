package org.github.core.modules.cache;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by github on 2017/3/11.
 */
@Component
public class SimpleCacheManage<V> implements ISimpleCacheManage<V> {
    private ISimpleCacheManage iveCacheManage;

    public ISimpleCacheManage getIveCacheManage() {
        return iveCacheManage;
    }

    public void setIveCacheManage(ISimpleCacheManage iveCacheManage) {
        this.iveCacheManage = iveCacheManage;
    }

    @Override
    public V get(String cachename, String key) {
        return (V) iveCacheManage.get(cachename, key);
    }

    @Override
    public List<V> like(String cachename, String key, int maxResults) {
        return iveCacheManage.like(cachename, key, maxResults);
    }

    @Override
    public void expire(String cachename, String key, int timeToLiveSeconds) {
        iveCacheManage.expire(cachename, key, timeToLiveSeconds);
    }

    @Override
    public boolean remove(String cachename, String key) {
        return iveCacheManage.remove(cachename, key);
    }

    @Override
    public void clearExpired() {
        iveCacheManage.clearExpired();
    }

    @Override
    public void put(String cachename, String key, V value, int timeToLiveSeconds) {
        iveCacheManage.put(cachename, key, value, timeToLiveSeconds);
    }
}
