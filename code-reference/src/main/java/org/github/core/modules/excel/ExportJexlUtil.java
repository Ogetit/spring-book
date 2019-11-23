package org.github.core.modules.excel;

import org.apache.commons.jexl2.*;
import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.functions.Functions;
import org.github.core.modules.utils.Reflections;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by github on 2016/10/30.
 */
public class ExportJexlUtil {
    private static JexlEngine engine;
    static {
        engine = new JexlEngine();
        Map<String, Object> funcs = new HashMap<String, Object>();
        funcs.put("fn", new Functions());
        funcs.put("vfn",new ExportFunctions());
        engine.setFunctions(funcs);
    }
    /**
     * 根据属性或key取值 支持jexl表达式语言
     * 示例见 jp_cjbl_report_page.jsp文件display:column标签的expfield属性
     * @param bean
     * @param property
     * @return [参数说明]
     *
     * @return Object [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static Object evaluateVal(Object bean, String property) {
        Object val = null;
        if (bean instanceof Map) {
            String str = null;
            if (property.indexOf("{") > -1 && property.indexOf("}") > -1) {
                property= StringUtils.replace(property,"''","'");
                JexlContext context = new MapContext((Map) bean);
                str = property;
                while (property.indexOf("{") > -1 && property.indexOf("}") > -1) {
                    int start = property.indexOf("{");
                    int end = property.indexOf("}");
                    String sub = property.substring(start, end + 1);
                    Expression expression = engine.createExpression(sub.substring(1,sub.length() - 1));
                    property = property.replace(sub, "");
                    str = str.replace(sub, expression.evaluate(context).toString());
                }
                val=str;
            } else {
                val = ((Map) bean).get(property);
            }
        } else {
            if (property.indexOf("{") > -1 && property.indexOf("}") > -1) {
                property= StringUtils.replace(property,"''","'");
                JexlContext context = new ObjectContext<Object>(engine, bean);
                String _val = property;
                while (property.indexOf("{") > -1 && property.indexOf("}") > -1) {
                    int start = property.indexOf("{");
                    int end = property.indexOf("}");
                    String sub = property.substring(start, end + 1);
                    Expression expression = engine.createExpression(sub.substring(1, sub.length() - 1));
                    property = property.replace(sub, "");
                    _val = _val.replace(sub, expression.evaluate(context).toString());
                }
                val = _val;
            } else {
                val= Reflections.invokeGetter(bean,property);
            }
        }
        return val;
    }
}
