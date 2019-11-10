package com.github.app.util.json.jackson;

import java.io.IOException;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 文件描述 基于 jackson 的 json 工具
 *
 * @author ouyangjie
 * @Title: JacksonUtil
 * @ProjectName spring-book
 * @date 2019/11/8 4:28 PM
 */
public class JacksonUtil {
    public static final CustomObjectMapper MAPPER = new CustomObjectMapper();

    public JacksonUtil() {
    }

    public static <T> String toJson(T t) {
        try {
            if (t != null) {
                return MAPPER.writeValueAsString(t);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            if (!StringUtils.isEmpty(json)) {
                return MAPPER.readValue(json, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            if (!StringUtils.isEmpty(json)) {
                return MAPPER.readValue(json, typeReference);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T clone(T t) {
        if (null == t) {
            return null;
        } else {
            String json = toJson(t);
            return (T) fromJson(json, t.getClass());
        }
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
