package com.github.spring.web.handler.converter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.app.pojo.vo.AjaxResult;

/**
 * 异常拦截处理
 */
@Component
public class CustomExceptionHandler implements HandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {

        // 如果是方法请求而且是@ResponseBody的请求
        if (handler instanceof HandlerMethod &&
                (AnnotationUtils.findAnnotation(((HandlerMethod) handler).getMethod(), ResponseBody.class) != null
                         || AnnotationUtils.findAnnotation(((HandlerMethod) handler).getBeanType(), RestController.class) != null)) {

            if (exception instanceof NumberFormatException) {
                exception = new IllegalArgumentException("不符合数字格式。", exception);
            } else if (exception instanceof DuplicateKeyException) {
                exception = new IllegalArgumentException("不符合数据唯一性规则。", exception);
            } else if (exception instanceof EmptyResultDataAccessException) {
                exception = new IllegalArgumentException("目标数据不存在。", exception);
            } else if (exception instanceof IncorrectResultSizeDataAccessException) {
                exception = new IllegalArgumentException("目标数据与期望不一致。", exception);
            }
            String resultJson;
            try {
                resultJson = JSON.toJSONString(AjaxResult.errorResult(exception), SerializerFeature.DisableCircularReferenceDetect);;
                // 接入移动端的json返回结果不同
                response.setContentType("application/json; charset=UTF-8");
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.getWriter().write(resultJson);
                response.getWriter().flush();
                response.getWriter().close();
                return new ModelAndView();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return new ModelAndView("error/500");
    }

}
