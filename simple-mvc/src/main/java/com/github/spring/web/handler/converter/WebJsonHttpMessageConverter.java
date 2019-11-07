package com.github.spring.web.handler.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 文件描述 Web层转化器 Json转对象，对象转Json
 *
 * @author ouyangjie
 * @Title: WebJsonHttpMessageConverter
 */
public class WebJsonHttpMessageConverter extends FastJsonHttpMessageConverter {
    /**
     * jackson 解析工具
     */
    private ObjectMapper mapper = new ObjectMapper();

    public WebJsonHttpMessageConverter() {
        super();
        // this.getFastJsonConfig().getSerializeConfig().put(Json.class, SwaggerJsonSerializer.instance);
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        if (obj instanceof springfox.documentation.spring.web.json.Json
                || obj instanceof springfox.documentation.swagger.web.UiConfiguration) {
            writeJsonStrToJson(obj, outputMessage);
        } else {
            super.writeInternal(obj, outputMessage);
        }
    }

    /**
     * 将json字符串转化成json
     *
     * @param obj
     * @param outputMessage
     *
     * @throws IOException
     */
    private void writeJsonStrToJson(Object obj, HttpOutputMessage outputMessage) throws IOException {
        HttpHeaders headers = outputMessage.getHeaders();
        ByteArrayOutputStream outnew = new ByteArrayOutputStream();
        mapper.writeValue(outnew, obj);
        outnew.flush();
        headers.setContentLength(outnew.size());
        OutputStream out = outputMessage.getBody();
        outnew.writeTo(out);
        outnew.close();
    }
}
