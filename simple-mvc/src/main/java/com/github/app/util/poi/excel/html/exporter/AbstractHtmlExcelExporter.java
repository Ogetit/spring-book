package com.github.app.util.poi.excel.html.exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

import com.github.app.util.poi.excel.html.css.bean.XlsCssStyle;
import com.github.app.util.poi.excel.html.util.Table2Excel;

/**
 * 文件描述 html转excel抽象类
 *
 * @author ouyangjie
 * @Title: AbstractHtmlExcelExporter
 * @date 2019/9/2 12:51 PM
 */
public abstract class AbstractHtmlExcelExporter {
    /**
     * 用于导出的文件名
     */
    private String title;
    /**
     * 是否 xlsx
     */
    private boolean xlsxFile = true;

    public AbstractHtmlExcelExporter(String title, String html) {
        Assert.notNull(title, "文件名为空！");
        this.title = title;
        Assert.notNull(html, "html内容为空！");
    }

    /**
     * 导出 Excel
     */
    public void exportExcel(String html, HttpServletResponse response) throws IOException {
        OutputStream output = response.getOutputStream();
        try {
            Assert.notNull(title, "业务标题为空！");
            String type = new String(title.getBytes("GB2312"), "ISO8859-1");
            String disposition = "attachment; filename=" + type.replaceAll(" ", "_").replaceAll(",", "_");
            disposition += xlsxFile ? ".xlsx" : ".xls";
            response.setHeader("Content-Disposition", disposition);
            if (xlsxFile) {
                response.setContentType("application/x-excel");
            } else {
                response.setContentType("application/vnd.ms-excel");
            }
            Workbook workbook = createWorkbook();
            new Table2Excel(workbook.createSheet()).html2sheet(html, this.getDefaultStyleList());
            workbook.write(output);
            output.flush();
            output.close();
        } catch (Exception e) {
            if (output != null) {
                response.reset();
            }
            throw new IllegalArgumentException("导出Excel出错，原因:" + e.getMessage());
        }
    }

    /**
     * 自定义html样式覆盖
     *
     * @return
     */
    public abstract List<XlsCssStyle> getDefaultStyleList();

    /**
     * 创建 Workbook
     *
     * @return
     *
     * @throws IOException
     */
    protected Workbook createWorkbook() throws IOException {
        Workbook book;
        if (this.xlsxFile) {
            book = new XSSFWorkbook();
        } else {
            book = new HSSFWorkbook();
        }
        return book;
    }
}
