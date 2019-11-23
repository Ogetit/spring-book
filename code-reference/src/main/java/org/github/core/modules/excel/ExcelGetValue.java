package org.github.core.modules.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by github on 2017/2/27.
 */
public class ExcelGetValue {
    private static final Logger logger = LoggerFactory.getLogger(ExcelGetValue.class);
    private String key;
    private Field field;

    public ExcelGetValue(Field field) {
        this.field = field;
    }

    public ExcelGetValue(String key) {
        this.key = key;
    }

    Object get(Object o) {
        if (o == null) {
            return null;
        }
        try {
            if (field != null) {
                return field.get(o);
            } else if (key != null) {
                return ((Map) o).get(key);
            }
        } catch (Exception e) {
            logger.error("反射获取" + o.getClass().getName() + "失败");
        }
        return null;
    }
}
