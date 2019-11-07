package com.github.spring.web.handler.argument;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * 文件描述 将泛型注解放到最高优先级解析
 *
 * @author ouyangjie
 * @Title: ConfigFirstGenericsParamArgumentResolver
 */
public class ConfigFirstGenericsParamArgumentResolver {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Autowired
    private WebGenericsParamArgumentResolver genericsJsonResolver;

    @PostConstruct
    public void injectSelfMethodArgumentResolver() {
        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
        argumentResolvers.add(genericsJsonResolver);
        argumentResolvers.addAll(requestMappingHandlerAdapter.getArgumentResolvers());
        requestMappingHandlerAdapter.setArgumentResolvers(argumentResolvers);
    }
}
