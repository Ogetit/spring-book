package com.github.app.util.poi.excel;

/**
 * 文件描述 Excel文件导入导出工具
 *
 * @author ouyangjie
 * @Title: XlsExcelRender
 */
public class XlsExcelRender<T> extends ExcelRender<T> {
    public XlsExcelRender() {
        super();
        this.setXlsxFile(false);
    }

    public XlsExcelRender(Class<T> clazz) {
        super(clazz);
        this.setXlsxFile(false);
    }
}
