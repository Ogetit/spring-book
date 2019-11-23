package org.github.core.modules.excel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置与excel表头
 *
 * @author 章磊
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface ExcelTitle {
    /**
     * 单元格表头名
     *
     * @return
     */
    String value();

    /**
     * 单元格描叙信息
     *
     * @return
     */
    String description() default "";

    /**
     * 单元格实例信息
     *
     * @return
     */
    String example() default "";

    /**
     * 单元格合并的表头名，相同表头的合并在一起
     *
     * @return
     */
    String group() default "";

    /**
     * 单元格颜色,相同group的颜色如果没有定义，则取第一个group的
     *
     * @return
     */
    String color() default "";

    /**
     * 列的宽度0为隐藏,多少个汉字宽度，如果这列是8个汉字那么输入8，默认宽度是汉字个数宽度
     *
     * @return
     */
    String width() default "";

    /**
     * 设置数据有效性  datavalid = {"按价格","按比例"}
     * @return
     */
    String[] datavalid() default {};

}
