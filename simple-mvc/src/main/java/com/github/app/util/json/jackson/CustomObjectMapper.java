package com.github.app.util.json.jackson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * 文件描述 自定义 jackson 解析器
 *
 * @author ouyangjie
 * @Title: CustomObjectMapper
 * @ProjectName spring-book
 * @date 2019/11/8 4:30 PM
 */
public class CustomObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 1L;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    final JsonSerializer<Date> dateSerializer = new JsonSerializer<Date>() {
        @Override
        public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
            if (value instanceof java.sql.Date) {
                jsonGenerator.writeString(CustomObjectMapper.this.dateFormat.format(value));
            } else {
                jsonGenerator.writeString(CustomObjectMapper.this.timeFormat.format(value));
            }

        }
    };

    public CustomObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, this.dateSerializer);
        this.registerModule(module);
        this.setDateFormat(this.timeFormat);
    }
}
