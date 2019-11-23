package org.github.core.modules.mybatis.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AsmsField {
	/**
	 * 
	 * @return 字段名
	 */
	String name();
	/**
	 * 
	 * @return 是否记录日志，默认为记录
	 */
	boolean log() default true;
	/**
	 * 字段值枚举  "0","不允许","1","允许"
	 * @return
	 */
	String[] options() default {};
	/**
	 * 比较字段值标记,如果有此标记的字段调用equals方法，比较是否为同一条记录
	 * @return
	 */
	boolean equalField() default false;

	/**
	 * 标记这个字段是不是需要记录的业务单号
	 * @return
	 */
	boolean isywdh() default false;
}
