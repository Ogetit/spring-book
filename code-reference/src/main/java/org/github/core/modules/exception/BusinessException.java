package org.github.core.modules.exception;


/**
 * 统一异常
 * 当action有异常的时候直接抛出去即可
 * 会统一捕获输出
 *
 * @author 章磊
 */
public class BusinessException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -5320363526440721079L;

    /**
     * 文本替换的参数
     */
    private Object[] args;
    /**
     * 错误代码
     */
    private Code code;
    /**
     * 展示给客户端的提示
     */
    private String resultTips;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造
     *
     * @param code  错误代码
     * @param cause 异常
     * @param args  文本替换的参数
     */
    public BusinessException(Code code, Throwable cause, Object... args) {
        super(cause);
        this.args = args;
        this.code = code;
    }

    /**
     * 构造
     *
     * @param code 错误代码
     * @param args 文本替换的参数
     */
    public BusinessException(Code code, Object... args) {
        this.code = code;
        this.args = args;
    }

    /**
     *  请勿使用，这是为了兼容而保留的。
     *
     * @param resultTips 展示给客户端的提示
     * @param code       错误代码
     * @param args       文本替换的参数
     */
    @Deprecated
    public BusinessException(String resultTips, Code code, Object... args) {
        this.resultTips = resultTips;
        this.code = code;
        this.args = args;
    }

    /**
     * @return 错误代码
     */
    public Code getCode() {
        return code;
    }

    /**
     * @param code 错误代码
     */
    public void setCode(Code code) {
        this.code = code;
    }


    /**
     * @return 文本替换的参数
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 重写错误提示
     */
//	public String getMessage() {
//		if(code==null || args==null){
//			return super.getMessage();
//		}
//		return String.format(code.getMessage(), args);
//	}
    public String getMessage() {
        if (code != null) {
            if (args == null || args.length < 1) {
                args = new Object[]{" "};
            }
            if (this.getCause() != null) {
                return String.format(code.getMessage() + "" + this.getCause().getMessage(), args);
            } else {
                return String.format(code.getMessage(), args);
            }
        } else if (this.getCause() != null) {
            return this.getCause().getMessage();
        } else {
            return super.getMessage();
        }
    }


    public String getResultTips() {
        return resultTips;
    }

    public void setResultTips(String resultTips) {
        this.resultTips = resultTips;
    }

}
