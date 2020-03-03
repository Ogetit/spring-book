package com.github.app.util.poi.excel.bean;

/**
 * 文件描述 导入读取注解
 *
 * @author ouyangjie
 * @Title: Excel
 */
public class ExcelItem {
    /**
     * 字段名
     *
     * @return
     */
    private String fieldName;

    /**
     * Excel中的列名
     *
     * @return
     */
    private String name;

    /**
     * 属性类型 默认 String
     */
    private Class<?> type = String.class;

    /**
     * 列名对应的A,B,C,D...,不指定按照默认顺序排序
     *
     * @return
     */
    private String column = "";

    /**
     * 列名对应的A,B,C,D..., 下标，0，1，2，3...
     *
     * @return
     */
    private int columnIndex = -1;

    /**
     * 提示信息
     *
     * @return
     */
    private String prompt = "";

    /**
     * 设置只能选择不能输入的列内容
     *
     * @return
     */
    private String[] readonlyCols = {};

    /**
     * 是否导出数据
     *
     * @return
     */
    private boolean isExport = true;

    /**
     * 是否为重要字段（整列标红,着重显示）
     *
     * @return
     */
    private boolean isMark = false;

    /**
     * 是否为数字
     *
     * @return
     */
    private boolean isNumber = false;

    /**
     * 是否为百分号数字
     *
     * @return
     */
    private boolean isPercentNumber = false;

    /**
     * 是否合计当前列
     *
     * @return
     */
    private boolean isSum = false;

    /**
     * 读取值的时候是否只读显示值
     *
     * @return
     */
    private boolean readShowValue = false;

    /**
     * 字段字节长度限制
     *
     * @return
     */
    private int byteLength = -1;

    /**
     * 是否设置日期数据格式化
     *
     * @return
     */
    private String dateFormat = "";

    /**
     * 是否 忽略 从Excel数据的读取
     *
     * @return
     */
    private boolean ignoreDataFromExcel = false;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String[] getReadonlyCols() {
        return readonlyCols;
    }

    public void setReadonlyCols(String[] readonlyCols) {
        this.readonlyCols = readonlyCols;
    }

    public boolean isExport() {
        return isExport;
    }

    public void setExport(boolean export) {
        isExport = export;
    }

    public boolean isMark() {
        return isMark;
    }

    public void setMark(boolean mark) {
        isMark = mark;
    }

    public boolean isNumber() {
        return isNumber;
    }

    public void setNumber(boolean number) {
        isNumber = number;
    }

    public boolean isPercentNumber() {
        return isPercentNumber;
    }

    public void setPercentNumber(boolean percentNumber) {
        isPercentNumber = percentNumber;
    }

    public boolean isSum() {
        return isSum;
    }

    public void setSum(boolean sum) {
        isSum = sum;
    }

    public boolean isReadShowValue() {
        return readShowValue;
    }

    public void setReadShowValue(boolean readShowValue) {
        this.readShowValue = readShowValue;
    }

    public int getByteLength() {
        return byteLength;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isIgnoreDataFromExcel() {
        return ignoreDataFromExcel;
    }

    public void setIgnoreDataFromExcel(boolean ignoreDataFromExcel) {
        this.ignoreDataFromExcel = ignoreDataFromExcel;
    }
}
