package org.github.core.modules.cache;

/**
 * Created by github on 2017/3/12.
 */
public class SimpleCacheName {
    /**
     * 缓存使用的名称
     */
    private String cachename;
    /**
     * 缓存失效时间 单位秒钟，表示多久时间后失效，如果为0 表示永久有效
     */
    private int timeToLiveSeconds;

    /**
     * 构造一个缓存名称
     * @param cachename 缓存使用的名称
     * @param timeToLiveSeconds 缓存失效时间 单位秒钟，表示多久时间后失效，如果为0 表示永久有效
     */
    public SimpleCacheName(String cachename, int timeToLiveSeconds) {
        this.cachename = cachename;
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    public String getCachename() {
        return cachename;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }
}
