package org.github.core.modules.cache;

import org.github.core.modules.mapper.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JavaType;

@Component
public class CacheManage {
	@Autowired
	private SimpleCacheManage ehcacheManage;
	@Autowired
	private RedisManage redisManage;
	JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();

	public void put(String cachename, String key, Class cls, Object value, int m) {
		if (cls != null) {
			redisManage.put(cachename, key, jsonMapper.toJson(value), m);
		} else {
			redisManage.put(cachename, key, value, m);
		}
	}

	public Object get(String cachename, String key, Class cls) {
		String localKey = cachename+key;
		Object t = ehcacheManage.get("TmpCache", localKey);
		if (t != null) {
			return t;
		}
		String json = (String) redisManage.get(cachename, key);
		if (json != null) {
			if (cls != null) {
				t = jsonMapper.fromJson(json, cls);
				ehcacheManage.put("TmpCache", localKey, t,20);
			} else {
				t = json;
				ehcacheManage.put("TmpCache", localKey, json,20);
			}
		}
		return t;
	}

	public Object get(String cachename, String key, JavaType javaType) {
		String localKey = cachename+key;
		Object t = ehcacheManage.get("TmpCache", localKey);
		if (t != null) {
			return t;
		}
		String json = (String) redisManage.get(cachename, key);
		if (json != null) {
			t = jsonMapper.fromJson(json, javaType);
			ehcacheManage.put("TmpCache", localKey, t,20);
		}
		return t;
	}
}
