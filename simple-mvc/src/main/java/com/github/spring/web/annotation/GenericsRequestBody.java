package com.github.spring.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 文件描述 含有泛型对象参数注解
 *
 * @author ouyangjie
 * @Title: GenericsRequestBody
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
@Import(RequestBody.class)
public @interface GenericsRequestBody {
}
