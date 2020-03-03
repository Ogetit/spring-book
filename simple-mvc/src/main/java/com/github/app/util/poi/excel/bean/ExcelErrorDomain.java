package com.github.app.util.poi.excel.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件描述 Excel错误信息对象
 *
 * @author ouyangjie
 * @Title: ExcelErrorDomain
 */
@ApiModel("Excel错误信息对象")
public class ExcelErrorDomain {
    @ApiModelProperty("sheet页名")
    private String sheetName;
    @ApiModelProperty("行")
    private String row;
    @ApiModelProperty("列")
    private String column;
    @ApiModelProperty("单元格")
    private String cell;
    @ApiModelProperty("错误提示")
    private String errorTip;

    public ExcelErrorDomain() {
    }

    public ExcelErrorDomain(String sheetName, String row, String column, String errorTip) {
        this.sheetName = sheetName;
        this.row = row;
        this.column = column;
        this.errorTip = errorTip;
    }

    public ExcelErrorDomain(String sheetName, int row, String column, String errorTip) {
        this.sheetName = sheetName;
        this.row = "" + row;
        this.column = column;
        this.errorTip = errorTip;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getCell() {
        if (cell == null) {
            cell = "" + column + row;
        }
        return cell;
    }

    public String getErrorTip() {
        return errorTip;
    }

    public void setErrorTip(String errorTip) {
        this.errorTip = errorTip;
    }
}
