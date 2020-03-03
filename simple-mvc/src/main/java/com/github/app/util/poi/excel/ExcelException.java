package com.github.app.util.poi.excel;

import java.util.List;

import com.github.app.util.poi.excel.bean.ExcelErrorDomain;

public class ExcelException extends Exception {
    private static final long serialVersionUID = -182346704447702807L;

    private List<ExcelErrorDomain> errorList;

    public ExcelException() {
    }

    public ExcelException(List<ExcelErrorDomain> errorList, String message) {
        super(message);
        this.errorList = errorList;
    }

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }

    public List<ExcelErrorDomain> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ExcelErrorDomain> errorList) {
        this.errorList = errorList;
    }
}