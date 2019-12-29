package com.github.app.util.poi.excel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文件描述 导入读取注解
 *
 * @author ouyangjie
 * @Title: Excel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface Excel {
    /**
     * Excel中的列名
     *
     * @return
     */
    String name();
    /**
     * 列名对应的A,B,C,D...,不指定按照默认顺序排序
     *
     * @return
     */
    String column() default "";
    /**
     * 提示信息
     *
     * @return
     */
    String prompt() default "";

    /**
     * 设置只能选择不能输入的列内容
     *
     * @return
     */
    String[] readonlyCols() default {};
    /**
     * 是否导出数据
     *
     * @return
     */
    boolean isExport() default true;
    /**
     * 是否为重要字段（整列标红,着重显示）
     *
     * @return
     */
    boolean isMark() default false;
    /**
     * 是否为数字
     *
     * @return
     */
    boolean isNumber() default false;
    /**
     * 是否为百分号数字
     *
     * @return
     */
    boolean isPercentNumber() default false;
    /**
     * 是否合计当前列
     *
     * @return
     */
    boolean isSum() default false;
    /**
     * 读取值的时候是否只读显示值
     *
     * @return
     */
    boolean readShowValue() default false;
    /**
     * 字段字节长度限制
     *
     * @return
     */
    int byteLength() default -1;
    /**
     * 是否设置日期数据格式化
     * @return
     */
    String dateFormat() default "";
    /**
     * 是否 忽略 从Excel数据的读取
     * @return
     */
    boolean ignoreDataFromExcel() default false;

}
