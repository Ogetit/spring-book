package com.github.spring.web.handler.argument;

import static org.springframework.web.bind.support.WebArgumentResolver.UNRESOLVED;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.github.spring.web.annotation.GenericsRequestBody;
import com.github.app.util.servlet.request.WebRequestUtil;

/**
 * 文件描述 含有泛型对象参数json转对象
 *
 * @author ouyangjie
 * @Title: WebGenericsParamArgumentResolver
 */
public class WebGenericsParamArgumentResolver implements HandlerMethodArgumentResolver {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private static final String CONTENT_TYPE = "application/json";

    private final Logger logger = LoggerFactory.getLogger(WebGenericsParamArgumentResolver.class);

    public WebGenericsParamArgumentResolver() {}

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(GenericsRequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory)
            throws Exception {
        // 获取JSON字符串
        String jsonStr;
        // 判断 content-type 是否是 application/json 的数据类型
        String contentType = nativeWebRequest.getHeader("content-type");
        if (!StringUtils.isEmpty(contentType) && contentType.contains(CONTENT_TYPE)) {
            jsonStr = WebRequestUtil.getJsonParam(nativeWebRequest);
        } else {
            jsonStr = WebRequestUtil.parseParamToJsonStr(nativeWebRequest);
        }
        try {
            Object result;
            Type originType = methodParameter.getGenericParameterType();
            if (originType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) originType;
                ParameterizedTypeImpl fastjsonType = new ParameterizedTypeImpl(type.getActualTypeArguments(), type.getOwnerType(), type.getRawType());
                result = JSON.parseObject(jsonStr, fastjsonType);
            } else {
                result = JSON.parseObject(jsonStr, originType);
            }
            return result;
        } catch (JSONException e) {
            logger.error("resolveArgument中JSON.parseObject方法出错：" + e.getMessage(), e);
            return UNRESOLVED;
        }
    }
}
