package com.github.app.common.exception;

import io.swagger.models.auth.In;

/**
 * 文件描述 视图展示异常
 *
 * @author ouyangjie
 * @Title: ViewException
 * @ProjectName spring-book
 * @date 2019/11/14 4:19 PM
 */
public class ViewException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static enum TYPE {
        // 弹层
        LAYER,
        // 跳转页面
        PAGE;
    }

    private TYPE type = TYPE.PAGE;

    protected ViewException() {
    }

    protected ViewException(Throwable cause) {
        super(cause);
    }

    protected ViewException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ViewException(String message) {
        super(message);
    }

    public ViewException type(TYPE type) {
        this.type = type;
        return this;
    }

    public TYPE getType() {
        return type;
    }
}
