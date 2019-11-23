package org.github.core.modules.exception;

/**
 * 只是提示
 * Created by Thinkpad on 2016/10/28.
 */
public class BusinessPromptException extends  BusinessException {

    public BusinessPromptException(String message) {
        super(message);
    }

    public BusinessPromptException(Throwable cause) {
        super(cause);
    }

    public BusinessPromptException(Code code, Throwable cause, Object... args) {
        super(code, cause, args);
    }

    public BusinessPromptException(Code code, Object... args) {
        super(code, args);
    }
}
