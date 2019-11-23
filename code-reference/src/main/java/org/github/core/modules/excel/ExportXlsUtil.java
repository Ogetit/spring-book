package org.github.core.modules.excel;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.github.core.modules.utils.DateUtil;
import org.github.core.modules.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class ExportXlsUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExportXlsUtil.class);

    /**
     * 根据集合创建data行
     *
     * @param list
     * @param fieldMap
     * @param sheet
     * @param dataRowCellStyle
     */
    public static void createRowList(Collection<?> list, Map<ExcelTitleBean, ExcelGetValue> fieldMap, HSSFSheet sheet, HSSFCellStyle dataRowCellStyle) {
        try {
            int rowNum = sheet.getLastRowNum() + 1;
            for (Object o : list) {
                HSSFRow datarow = sheet.createRow(rowNum++);
                int dataCellNum = 0;
                for (ExcelTitleBean headText : fieldMap.keySet()) {
                    HSSFCell datacell = datarow.createCell(dataCellNum);
                    datacell.setCellStyle(dataRowCellStyle);
                    ExcelGetValue filed = fieldMap.get(headText);
                    Object data = filed.get(o);
                    setCellValue(datacell, data);
                    dataCellNum++;
                }
            }
        } catch (Exception e) {
            logger.error("导出Excel失败", e);
        }
    }

    /**
     * 通过class创建表头,并返回sheet
     *
     * @param fieldMap
     * @param workbook
     * @return
     */
    public static HSSFSheet createHead(Map<ExcelTitleBean, ExcelGetValue> fieldMap, Object firstObj, HSSFWorkbook workbook) {
        int sheetcount = workbook.getNumberOfSheets();
        String sheetTitleAn = ExcelFieldUtil.getClassTitle(firstObj);
        String sheetTitle = "";
        if (StringUtils.isNotBlank(sheetTitleAn)) {
            sheetTitle = sheetTitleAn;
        }
        // 解析出list中的bean的注解和属性名字
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet((StringUtils.isBlank(sheetTitle) ? "" : sheetTitle + "_") + "Sheet" + (sheetcount + 1));
        // 生成表格头单元格样式
        HSSFCellStyle headCellStyle = getHeadCellStyle(workbook);

        int rowNum = 0;
        boolean hasExample = false;
        boolean hasDescription = false;
        boolean hasGroup = false;
        List<ExcelTitleBean> fieldList = new ArrayList<ExcelTitleBean>(fieldMap.keySet());
        int filedSize = fieldList.size();
        // 判断bean中是否有合并表头、示例、描叙
        for (int i = 0; i < filedSize; i++) {
            ExcelTitleBean exceltitle = fieldList.get(i);
            if (StringUtils.isNotBlank(exceltitle.getGroup())) {
                hasGroup = true;
            }
            if (StringUtils.isNotBlank(exceltitle.getExample())) {
                hasExample = true;
            }
            if (StringUtils.isNotBlank(exceltitle.getDescription())) {
                hasDescription = true;
            }
        }
        int headCellNum = 0;
        // 产生表格标题行
        HSSFRow headrow = null;
        HSSFRow grouprow = null;
        // 如果有合并表头，则创建2行，一行是合并表头，一行是列名
        if (hasGroup) {
            grouprow = sheet.createRow(0);
            headrow = sheet.createRow(1);
            rowNum++;
        } else {
            headrow = sheet.createRow(0);
        }
        String groupPreText = "";
        Integer groupPreCol = 0;
        for (int i = 0; i < filedSize; i++) {
            ExcelTitleBean exceltitle = fieldList.get(i);
            HSSFCell headcell = headrow.createCell(headCellNum);
            headcell.setCellStyle(headCellStyle);
            headcell.getCellStyle().setWrapText(true);
            String groupText = StringUtils.trimToEmpty(exceltitle.getGroup());
            // 处理表头合并
            if (hasGroup) {
                HSSFCell groupcell = grouprow.createCell(headCellNum);
                groupcell.setCellStyle(headCellStyle);
                groupcell.getCellStyle().setWrapText(true);
                if (StringUtils.isBlank(groupText)) {
                    // 如果本列没有合并表头属性，则把值写在group行，并且需要合并2行
                    headcell = groupcell;
                    CellRangeAddress cra1 = new CellRangeAddress(0, 1, headCellNum, headCellNum);
                    sheet.addMergedRegion(cra1);
                } else {
                    HSSFRichTextString richGroupText = new HSSFRichTextString(groupText);
                    groupcell.setCellValue(richGroupText);
                    if (groupPreText.equals(groupText)) {
                        // 不是最后一列，或者下一列的group不相等，则合并之前的
                        if (i == filedSize - 1 || !StringUtils.trimToEmpty(fieldList.get(i + 1).getGroup()).equals(groupText)) {
                            CellRangeAddress cra1 = new CellRangeAddress(0, 0, groupPreCol, headCellNum);
                            sheet.addMergedRegion(cra1);
                        }
                    } else {
                        groupPreCol = headCellNum;
                        groupPreText = groupText;
                    }
                }
            }

            String headText = StringUtils.trimToEmpty(exceltitle.getValue());
            HSSFRichTextString richHeadText = new HSSFRichTextString(headText);
            headcell.setCellValue(richHeadText);
            String fontColor = exceltitle.getColor();
            if (StringUtils.isNotEmpty(fontColor)){
                headcell.setCellStyle(getHeadCellStyle(workbook,fontColor));
                headcell.getCellStyle().setWrapText(true);
            }
            int withWord = headText.length();
            // 一个汉字8个
            if ("0".equals(exceltitle.getWidth())) {
                withWord = 0;
            } else if (StringUtils.isNotBlank(exceltitle.getWidth())) {
                withWord = NumberUtils.toInt(exceltitle.getWidth());
            }
            sheet.setColumnWidth(headCellNum, 100 * 8 * withWord);
            headCellNum++;
        }

        if (hasExample) {
            rowNum++;
            headrow = sheet.createRow(rowNum);
            headCellNum = 0;
            headCellStyle = getHeadCellStyle(workbook);
            for (ExcelTitleBean headText : fieldMap.keySet()) {
                HSSFCell cell = headrow.createCell(headCellNum++);
                cell.setCellStyle(headCellStyle);
              //  cell.getCellStyle().setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                cell.getCellStyle().setWrapText(true);
                HSSFRichTextString text = new HSSFRichTextString(headText.getExample());
                cell.setCellValue(text);
                String fontColor = headText.getColor();
                if (StringUtils.isNotEmpty(fontColor)){
                    cell.setCellStyle(getHeadCellStyle(workbook,fontColor));
                    cell.getCellStyle().setWrapText(true);
                }
                if (headText.getDatavalid() != null && headText.getDatavalid().length > 0) {
                    HSSFDataValidation data_validation_list = setDataValidationList(cell.getRowIndex(), cell.getRowIndex(), cell.getColumnIndex(), cell.getColumnIndex(), headText.getDatavalid());
                    sheet.addValidationData(data_validation_list);
                }
            }
        }
        if (hasDescription) {
            rowNum++;
            headrow = sheet.createRow(rowNum);
            headCellNum = 0;
            headCellStyle = getHeadCellStyle(workbook);
            for (ExcelTitleBean headText : fieldMap.keySet()) {
                HSSFCell cell = headrow.createCell(headCellNum++);
                cell.setCellStyle(headCellStyle);
             //   cell.getCellStyle().setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                cell.getCellStyle().setWrapText(true);
                String fontColor = headText.getColor();
                if (StringUtils.isNotEmpty(fontColor)){
                    cell.setCellStyle(getHeadCellStyle(workbook,fontColor));
                    cell.getCellStyle().setWrapText(true);
                }
                HSSFRichTextString text = new HSSFRichTextString(headText.getDescription());
                cell.setCellValue(text);
            }
        }
        rowNum++;
        setDataValidation(fieldMap, sheet, ExportXls.EXCELBECOUNT);
        return sheet;
    }

    private static void setDataValidation(Map<ExcelTitleBean, ExcelGetValue> fieldMap, HSSFSheet sheet, int EXCELBECOUNT) {
        int dataBeginRowNum = sheet.getLastRowNum() + 1;
        int dataCellNum = 0;
        for (ExcelTitleBean headText : fieldMap.keySet()) {
            if (headText.getDatavalid() != null && headText.getDatavalid().length > 0) {
                HSSFDataValidation data_validation_list = ExportXlsUtil.setDataValidationList(dataBeginRowNum, EXCELBECOUNT, dataCellNum, dataCellNum, headText.getDatavalid());
                sheet.addValidationData(data_validation_list);
            }
            dataCellNum++;
        }
    }

    /**
     * 按数据类型写入数据到cell
     *
     * @param cell excel 单元格
     * @param obj  写入cell的数据
     */
    private static void setCellValue(HSSFCell cell, Object obj) {
        cell.getCellStyle().setWrapText(false);
        if (obj instanceof String) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue((String) obj);
        } else if (obj instanceof Double) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Double) obj);
        } else if (obj instanceof Integer) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Integer) obj);
        } else if (obj instanceof Date) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(DateUtil.dateToStrLong((Date) obj));
        } else if (obj instanceof Number) {
            if (obj != null) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(((Number) obj).doubleValue());
            }
        } else {
            try {
                if (obj != null) {
                    cell.setCellValue(obj.toString());
                }
            } catch (Exception e) {
                cell.setCellValue("");
            }
        }
    }

    /**
     * 表头单元格样式
     *
     * @param workbook
     * @return
     */
    private static HSSFCellStyle getHeadCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//这个会导致在excel2003软件打开有黑色填充
        style.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);

        style.setBorderBottom(BorderStyle.THIN); // 下边框
        style.setBorderLeft(BorderStyle.THIN);// 左边框
        style.setBorderTop(BorderStyle.THIN);// 上边框
        style.setBorderRight(BorderStyle.THIN);// 右边框

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        // font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    private static HSSFCellStyle getHeadCellStyle(HSSFWorkbook workbook,String fontColor) {
        HSSFCellStyle cellStyle = getHeadCellStyle(workbook);
        try {
            cellStyle.getFont(workbook).setColor(new Short(fontColor));
        }catch(Exception e){
            logger.error("导出Excel失败color:"+fontColor+"不是一个颜色值", e);
        }
        return cellStyle;
    }

    /**
     * 数据单元格样式
     *
     * @param workbook
     * @return
     */
    public static HSSFCellStyle getDataRowCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style2 = workbook.createCellStyle();
        // style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
       // style2.setAlignment(HorizontalAlignment.LEFT);
        style2.setVerticalAlignment(VerticalAlignment.CENTER);
        //style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);//这个会导致在excel2003软件打开有黑色填充
        style2.setBorderBottom(BorderStyle.THIN);
        style2.setBorderLeft(BorderStyle.THIN);
        style2.setBorderRight(BorderStyle.THIN);
        style2.setBorderTop(BorderStyle.THIN);
        // 生成另一个字体
        // HSSFFont font2 = workbook.createFont();
        // font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        // style2.setFont(font2);
        return style2;
    }

    public static void writeFile(String pathFile, HSSFWorkbook workbook) throws IOException {
        String path = WebUtils.getRootPath(pathFile);
        File file = new File(path);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            workbook.write(out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 声明一个工作薄
     *
     * @param pathFile 如果文件存在则在这个文件上添加一个新的sheet
     */
    public static HSSFWorkbook getWorkbook(String pathFile) throws IOException {
        HSSFWorkbook workbook = null;
        if (StringUtils.isNotBlank(pathFile)) {
            String path = WebUtils.getRootPath(pathFile);
            File file = new File(path);
            if (file.exists()) {
                workbook = new HSSFWorkbook(new FileInputStream(file));
            }
        }
        if (workbook == null) {
            workbook = new HSSFWorkbook();
        }
        return workbook;
    }

    /**
     * @param firstRow
     * @param lastRow
     * @param firstCol
     * @param lastCol
     * @param textlist 设置下拉列表的内容
     * @return
     */
    public static HSSFDataValidation setDataValidationList(int firstRow, int lastRow, int firstCol, int lastCol, String[] textlist) {
        //加载下拉列表内容
        DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
        //设置数据有效性加载在哪个单元格上。

        //四个参数分别是：起始行、终止行、起始列、终止列
        //(int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        //数据有效性对象
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);

        return data_validation_list;
    }

}
