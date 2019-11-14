package com.github.app.pojo.vo;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.app.common.exception.ViewException;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * AjaxResult
 */
@ApiModel("统一Json返回对象")
public class AjaxResult<T> {
    private static final Logger logger = LoggerFactory.getLogger(AjaxResult.class);

    private static final Integer FAILURE = 1;
    private static final Integer SUCCESS = 0;
    private static final Integer ERROR = -1;

    private static final String SUCCESS_MSG = "success";

    @ApiModelProperty(value = "错误代码【成功(0)、失败(1)、异常(-1)】")
    private int errNo;
    @ApiModelProperty(value = "业务数据")
    private T data;
    @ApiModelProperty(value = "接口信息")
    private String message;
    @ApiModelProperty(value = "异常信息")
    private String exception;
    @ApiModelProperty(value = "重定向链接")
    private String redirectUrl;

    private AjaxResult() {
    }

    public AjaxResult(int code, String message, T data) {
        this.errNo = code;
        this.message = message;
        this.data = data;
    }

    public static AjaxResult emptySuccessResult() {
        AjaxResult result = new AjaxResult();
        result.setErrNo(SUCCESS);
        result.setMessage(SUCCESS_MSG);
        return result;
    }

    public static <T extends Object> AjaxResult<T> successResult(T data) {
        AjaxResult<T> result = new AjaxResult<T>();
        result.setErrNo(SUCCESS);
        result.setMessage(SUCCESS_MSG);
        result.setData(data);
        return result;
    }
    public static <T> AjaxResult<T> errorResult(Exception exception) {
        AjaxResult<T> result = new AjaxResult<T>();
        if (exception instanceof ViewException) {
            result.setErrNo(FAILURE);
            result.setMessage(exception.getMessage());
            result.setException(ExceptionUtils.getFullStackTrace(exception));
            logger.warn(exception.getMessage(), exception);
        } else {
            result.setErrNo(ERROR);
            result.setMessage(exception.getMessage());
            result.setException(ExceptionUtils.getFullStackTrace(exception));
            logger.error(exception.getMessage(), exception);
        }
        return result;
    }

    public static <T> AjaxResult<T> error300Result(String redirectUrl) {
        AjaxResult<T> object = new AjaxResult<T>(JsonResponseCode.ERROR300.getCode(), "", null);
        object.setRedirectUrl(redirectUrl);
        return object;
    }

    public static <T> AjaxResult<T> error300ResultByCode(int code) {
        AjaxResult<T> object = new AjaxResult<T>(JsonResponseCode.ERROR300.getCode(), "", null);
        String redirectUrl = object.getErrorUrlByCode(code);
        object.setRedirectUrl(redirectUrl);
        return object;
    }

    public static <T> AjaxResult<T> error301Result() {
        return new AjaxResult<T>(JsonResponseCode.ERROR301.getCode(), "", null);
    }

    public static <T> AjaxResult<T> error403Result() {
        return new AjaxResult<T>(JsonResponseCode.ERROR403.getCode(), "", null);
    }

    public static <T> AjaxResult<T> error404Result(Object data) {
        return new AjaxResult<T>(JsonResponseCode.ERROR404.getCode(), "", null);
    }

    public static <T> AjaxResult<T> error500Result(Object data) {
        return new AjaxResult<T>(JsonResponseCode.ERROR500.getCode(), "", null);
    }

    public static <T> AjaxResult<T> error503Result(Object data) {
        return new AjaxResult<T>(JsonResponseCode.ERROR503.getCode(), "", null);
    }

    public int getErrNo () {
        return errNo;
    }

    public void setErrNo (int errNo) {
        this.errNo = errNo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public static enum JsonResponseCode {

        SUCCESS(0),
        ERROR403(403),
        ERROR404(404),
        ERROR500(500),
        ERROR503(503),
        ERROR300(300),
        ERROR301(301),
        ERROR(400);

        int code;
        String url;

        JsonResponseCode(int code) {
            this.code = code;
            if (code == 200) {
                this.url = "/index";
            } else {
                this.url = "/error/" + code;
            }
        }

        public int getCode() {
            return code;
        }
        public String getUrl() {
            return url;
        }
    }

    public String getErrorUrlByCode(int code) {
        for (JsonResponseCode responseCode : JsonResponseCode.values()) {
            if (responseCode.getCode() == code) {
                return responseCode.getUrl();
            }
        }
        return "";
    }
}
