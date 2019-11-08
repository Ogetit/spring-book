package com.github.app.util.json.fastjson;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 文件描述 fastjson 工具类
 *
 * @author ouyangjie
 * @Title: FastjsonUtil
 * @ProjectName spring-book
 * @date 2019/11/8 10:45 PM
 */
public class FastjsonUtil {
    public static <T> String toJson(T object) {
        try {
            if (object != null) {
                // 对象直接循环引用隔断，解析了的对象不再解析
                return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            if (!StringUtils.isEmpty(json)) {
                // 忽略未匹配到的项
                return JSON.parseObject(json, clazz, Feature.IgnoreNotMatch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            if (!StringUtils.isEmpty(json)) {
                // 忽略未匹配到的项
                return JSON.parseObject(json, typeReference, Feature.IgnoreNotMatch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T clone(T object, Class<T> clazz) {
        try {
            if (object == null) {
                return null;
            }
            String json = toJson(object);
            return fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T,Y> Y copy(T t, Class<Y> clazz) {
        if (null == t) {
            return null;
        } else {
            String json = toJson(t);
            return fromJson(json, clazz);
        }
    }
}
