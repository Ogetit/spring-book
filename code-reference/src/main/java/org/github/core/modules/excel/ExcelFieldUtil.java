package org.github.core.modules.excel;

import org.apache.commons.lang.StringUtils;
import org.github.core.modules.utils.Reflections;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 解析ExcelTitle注解获取内容
 *
 * @author 章磊
 */
public class ExcelFieldUtil {
    public static void setProperty(Object o, String title, Object value) throws Exception {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelTitle.class)) {
                ExcelTitle excelTitle = field.getAnnotation(ExcelTitle.class);
                String tmpTitle = excelTitle.value();
                if (title.equals(tmpTitle)) {
                    Reflections.makeAccessible(field);
                    field.set(o, value);
                }
            }

        }
    }

    /**
     * 获取Bean中EXCELTITLE对应的方法，用于在使用使通过get(Object)方式取得值
     *
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Map<String, Field> getTitleFields(Class clazz) throws Exception {
        Map<String, Field> titleMap = new LinkedHashMap<String, Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExcelTitle.class)) {
                ExcelTitle excelTitle = field.getAnnotation(ExcelTitle.class);
                Reflections.makeAccessible(field);
                if (excelTitle != null && StringUtils.isNotBlank(excelTitle.value())) {
                    titleMap.put(excelTitle.value(), field);
                }
            }
        }
        return titleMap;
    }

    /**
     * 获取Bean中注释对应的方法，用于在使用使通过get(Object)方式取得值
     *
     * @param firstObj
     * @return
     * @throws Exception
     */
    public static Map<ExcelTitleBean, ExcelGetValue> getFields(Object firstObj) throws Exception {
        Map<ExcelTitleBean, ExcelGetValue> titleMap = new LinkedHashMap<ExcelTitleBean, ExcelGetValue>();
        if (firstObj instanceof Map) {
            Map<String, Object> dmap = (Map) firstObj;
            for (String key : dmap.keySet()) {
                if("SHEET_TITLE".equals(key)){
                    continue;
                }
                ExcelTitleBean eb = new ExcelTitleBean();
                eb.setValue(key);
                ExcelGetValue egv = new ExcelGetValue(key);
                titleMap.put(eb, egv);
            }
        } else {
            Class clazz = firstObj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ExcelTitle.class)) {
                    ExcelTitle excelTitle = field.getAnnotation(ExcelTitle.class);
                    ExcelTitleBean eb = new ExcelTitleBean();
                    eb.setValue(excelTitle.value());
                    eb.setDescription(excelTitle.description());
                    eb.setExample(excelTitle.example());
                    eb.setGroup(excelTitle.group());
                    eb.setColor(excelTitle.color());
                    eb.setWidth(excelTitle.width());
                    eb.setDatavalid(excelTitle.datavalid());
                    Reflections.makeAccessible(field);
                    ExcelGetValue egv = new ExcelGetValue(field);
                    titleMap.put(eb, egv);
                }
            }
        }
        return titleMap;
    }


    /**
     * 获取bean上注解
     *
     * @param o
     * @return
     */
    public static String getClassTitle(Object o) {
        if (o instanceof Map) {
            return StringUtils.trimToEmpty((String) ((Map) o).get("SHEET_TITLE"));
        } else {
            ExcelTitle excelTitle = o.getClass().getAnnotation(ExcelTitle.class);
            if (excelTitle != null) {
                return excelTitle.value();
            }
        }
        return "";
    }
}
