package org.github.core.modules.web.ajax;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import org.apache.commons.lang.StringEscapeUtils;
import org.github.core.modules.utils.StrUtil;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * 扩展spring的controller,页面请求json转对象的类
 * 当页面json属性少于类的属性时，原来的会报错，这个可以解决这个问题
 * this.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
 *
 * @author 章磊
 */
public class MappingJackson2HttpMessageExtConverter extends MappingJackson2HttpMessageConverter {
    public MappingJackson2HttpMessageExtConverter() {
        super();
        this.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(type, contextClass);
        Object o = this.readJavaType(javaType, inputMessage);
        return o;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(clazz, (Class) null);
        Object o = this.readJavaType(javaType, inputMessage);
//        if (o instanceof Map) {
//            Map m = (Map) o;
//            for (Object key : m.keySet()) {
//                Object vo = m.get(key);
//                if (vo != null && vo instanceof String) {
//                    m.put(key, StringEscapeUtils.escapeSql(StrUtil.clearHtmlSimple(vo.toString())));
//                }
//            }
//        }
        return o;
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            BufferedReader br = null;
            StringBuilder str = new StringBuilder();
            try {
                br = new BufferedReader(new InputStreamReader(inputMessage.getBody(), "UTF-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    str.append(line);
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }
            //防止单引号注入
            String json = StringEscapeUtils.escapeSql(StrUtil.clearHtmlSimple(str.toString()));
            return this.getObjectMapper().readValue(json, javaType);
        } catch (IOException var4) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + var4.getMessage(), var4);
        }
    }
}
