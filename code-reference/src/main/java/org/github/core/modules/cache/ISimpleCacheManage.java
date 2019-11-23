package org.github.core.modules.cache;

import java.util.List;

/**
 * Created by github on 2017/3/11.
 */
public interface ISimpleCacheManage<V> {
    void put(String cachename, String key, V value, int timeToLiveSeconds);

    V get(String cachename, String key);

    /**
     * 通配符。该通配符表示所有的意思。如：keys * 匹配数据库中所有 key 。
     * ?通配符。表示一个任意字符。如：keys h?llo 命令匹配 hello ， hallo 和 hxllo 等。
     * 通配符。表示任何字符。如：keys h*llo 表示任何一h开头，llo结尾的key，如匹配 hllo 和 heeeeello 等。
     *
     * @param cachename
     * @param key
     * @param maxResults    返回的数据条数
     * @return
     */
    List<V> like(String cachename, String key, int maxResults);

    /**
     * 设置失效时间
     *
     * @param cachename
     * @param key
     * @param timeToLiveSeconds
     */
    void expire(String cachename, String key, int timeToLiveSeconds);

    boolean remove(String cachename, String key);

    /**
     * 清除所有缓存中过期的元素
     */
    void clearExpired();


}
