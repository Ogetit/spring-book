package org.github.core.modules.mybatis.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsmsTable {
    /**
     * @return 是否记录日志，默认为不记录
     */
    boolean log() default false;

    /**
     * 主表名字
     * @return
     */
    String main() default "";


}
