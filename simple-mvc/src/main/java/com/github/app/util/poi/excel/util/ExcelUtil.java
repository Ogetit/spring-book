package com.github.app.util.poi.excel.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

/**
 * 文件描述 Excel工具类
 *
 * @author ouyangjie
 * @Title: ExcelUtil
 * @ProjectName ifs-dw-meg
 * @date 2020/2/26 3:46 PM
 */
public class ExcelUtil {
    /**
     * 将 EXCEL 中 A,B,C,D,E 列映射成 0,1,2,3
     *
     * @param col
     */
    public static int getExcelColIndex(String col) {
        col = col.toUpperCase();
        // 从 -1 开始计算,字母重 1 开始运算。这种总数下来算数正好相同。
        int count = -1;
        char[] cs = col.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
        }
        return count;
    }

    /**
     * 将 EXCEL 中 0,1,2,3 列映射成 A,B,C,D,E
     *
     * @param index
     */
    public static String getExcelColIndexStr(int index) {
        String result = "";
        int count = index + 1;
        do {
            result = ((char) ((count % 26) + 64)) + result;
            count = count / 26;
        } while (count > 0);
        return result.toUpperCase();
    }

    /**
     * 设置单元格上批注
     *
     * @param sheet          要设置的sheet.
     * @param commentTitle   标题
     * @param commentContent 内容
     *
     * @return 设置好的sheet.
     */
    public static Sheet setComment(Sheet sheet, Cell cell, String commentTitle, String commentContent, boolean xlsxFile) {
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 1, 1, 1, 1);
        Comment comment = drawing.createCellComment(anchor);
        if (xlsxFile) {
            comment.setString(new XSSFRichTextString(commentContent));
        } else {
            comment.setString(new HSSFRichTextString(commentContent));
        }
        comment.setAuthor(commentTitle);
        // comment.setVisible(true);
        cell.setCellComment(comment);
        return sheet;
    }

    /**
     * 创建 Workbook
     *
     * @param input
     *
     * @return
     *
     * @throws IOException
     */
    public static Workbook createWorkbook(InputStream input, boolean xlsxFile) throws IOException {
        Workbook book;
        if (xlsxFile) {
            book = null != input ? new XSSFWorkbook(input) : new XSSFWorkbook();
        } else {
            book = null != input ? new HSSFWorkbook(input) : new HSSFWorkbook();
        }
        return book;
    }

    /**
     * 添加 字数限制 数据
     *
     * @param sheet
     * @param lengthLimitMap
     */
    public static void addStrLengthLimit(Sheet sheet, int startRow, int endRow, Map<Integer, Integer> lengthLimitMap) {
        if (CollectionUtils.isEmpty(lengthLimitMap)) {
            return;
        }

        for (Map.Entry<Integer, Integer> item : lengthLimitMap.entrySet()) {
            // 构造constraint对象
            DataValidationHelper helper = sheet.getDataValidationHelper();
            DataValidationConstraint dvConstraint = helper.createNumericConstraint(
                    DataValidationConstraint.ValidationType.TEXT_LENGTH,
                    DataValidationConstraint.OperatorType.BETWEEN,
                    "1", item.getValue().toString());
            // 四个参数分别是：起始行、终止行、起始列、终止列
            CellRangeAddressList regions = new CellRangeAddressList(startRow, endRow, item.getKey(), item.getKey());
            // 数据有效性对象
            DataValidation data_validation_view = helper.createValidation(dvConstraint, regions);
            // data_validation_view.createPromptBox("温性提示", "请保持字数在【" + item.getValue() + "】以内");
            data_validation_view.createErrorBox("字数过长", "请保持字数在【" + item.getValue() + "】以内");
            data_validation_view.setShowErrorBox(true);
            sheet.addValidationData(data_validation_view);
        }
    }

    /**
     * 设置单元格上提示
     *
     * @param sheet         要设置的sheet.
     * @param promptTitle   标题
     * @param promptContent 内容
     * @param firstRow      开始行
     * @param endRow        结束行
     * @param firstCol      开始列
     * @param endCol        结束列
     *
     * @return 设置好的sheet.
     */
    public static Sheet setPrompt(Sheet sheet, String promptTitle, String promptContent, int firstRow,
                                  int endRow, int firstCol, int endCol) {
        // 构造constraint对象
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = helper.createCustomConstraint("DD1");
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        DataValidation data_validation_view = helper.createValidation(dvConstraint, regions);
        data_validation_view.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(data_validation_view);
        return sheet;
    }

    /**
     * 设置单元格上下拉框
     *
     * @param sheet    要设置的sheet
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     *
     * @return 设置好的sheet.
     */
    public static Sheet setPullDown(Sheet sheet, String[] textList, int firstRow,
                                    int endRow, int firstCol, int endCol) {
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 加载下拉列表内容
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
        constraint.setExplicitListValues(textList);
        // 数据有效性对象
        DataValidation data_validation_view = helper.createValidation(constraint, regions);
        sheet.addValidationData(data_validation_view);
        return sheet;
    }

    /**
     * 设置单元格上下拉框
     *
     * @param sheet    要设置的sheet
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     *
     * @return 设置好的sheet.
     */
    public static Sheet setPullDownMax(int index, Sheet sheet, Sheet selectSheet, String[] textList,
                                       int firstRow, int endRow, int firstCol, int endCol) {
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 设置数据有效性加载在哪个单元格上,参数分别是：从sheet2获取A1到A5000作为一个下拉的数据、起始行、终止行、起始列、终止列
        // 1、设置有效性
        String[] arr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        // String strFormula = "Sheet2!$A$1:$A$5000" ;
        // Sheet2第A,1到A,5000作为下拉列表来源数据
        String strFormula = selectSheet.getSheetName() + "!$" + arr[index] + "$1:$" + arr[index] + "$5000";
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createFormulaListConstraint(strFormula);
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.createErrorBox("Error", "Error");
        dataValidation.createPromptBox("", null);
        sheet.addValidationData(dataValidation);

        // 下拉列表元素很多的情况，从 sheet 里面取枚举值
        // 2、生成 selectSheet 内容
        for (int j = 0; j < textList.length; j++) {
            if (index == 0) {
                // 第1个下拉选项，直接创建行、列
                Row row = selectSheet.createRow(j);
                row.createCell(0).setCellValue(textList[j]);
            } else {
                // 非第1个下拉选项
                int rowCount = selectSheet.getLastRowNum();
                if (j <= rowCount) {
                    // 前面创建过的行，直接获取行，创建列
                    // 获取行，创建列 ， 设置对应单元格的值
                    selectSheet.getRow(j).createCell(index).setCellValue(textList[j]);

                } else {
                    // 未创建过的行，直接创建行、创建列
                    // 创建行、创建列
                    selectSheet.createRow(j).createCell(index).setCellValue(textList[j]);
                }
            }
        }
        return sheet;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet    要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     *
     * @return 设置好的sheet.
     */
    public static Sheet setValidation(Sheet sheet, String[] textlist, int firstRow, int endRow,
                                      int firstCol, int endCol) {
        // 加载下拉列表内容
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        DataValidation data_validation_list = helper.createValidation(constraint, regions);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }

    /**
     * 获取cell中的值并返回String类型
     *
     * @param cell
     *
     * @return String类型的cell值
     */
    public static String getCellValue(Cell cell, boolean readShowValue) {
        if (null == cell) {
            return "";
        }
        if (readShowValue) {
            // 得到显示值，比如 1 不会变成 1.0
            DataFormatter dataFormatter = new DataFormatter();
            return dataFormatter.formatCellValue(cell);
        }
        String cellValue = "";
        // 以下是判断数据的类型
        CellType cellType = cell.getCellType();
        if (cellType == CellType.NUMERIC) {
            // 判断单元格的类型是否则NUMERIC类型
            cellValue = getNumericValue(cell);
        } else if (cellType == CellType.STRING) {
            // 字符串
            cellValue = cell.getStringCellValue();
        } else if (cellType == CellType.BOOLEAN) {
            // Boolean
            cellValue = Objects.toString(cell.getBooleanCellValue());
        } else if (cellType == CellType.FORMULA) {
            // 公式
            try {
                // 如果公式结果为字符串
                cellValue = String.valueOf(cell.getStringCellValue());
            } catch (IllegalStateException e) {
                cellValue = getNumericValue(cell);
            }
        } else if (cellType == CellType.BLANK) {
            // 空值
            cellValue = "";
        } else if (cellType == CellType.ERROR) {
            // 故障
            cellValue = "非法字符";
        } else {
            cellValue = "";
        }
        return cellValue;
    }

    private static String getNumericValue(Cell cell) {
        String cellValue;
        if (DateUtil.isCellDateFormatted(cell)) {
            // 判断是否为日期类型
            Date date = cell.getDateCellValue();
            DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
            cellValue = formater.format(date);
        } else {
            FormulaEvaluator evaluator =
                    cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateFormulaCell(cell);
            // 有些数字过大，直接输出使用的是科学计数法： 2.67458622E8 要进行处理
            DecimalFormat df = new DecimalFormat("####.####");
            cellValue = df.format(cell.getNumericCellValue());
        }
        return cellValue;
    }

    /**
     * 对某个单元格加锁
     *
     * @param sheet
     * @param cell
     */
    public static void setDataLockValidation(Sheet sheet, Cell cell) {
        // 单元格设置数据有效性
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint =
                helper.createNumericConstraint(DataValidationConstraint.ValidationType.INTEGER,
                        DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, "0", null);
        // 设置数据有效性加载在哪个单元格上。
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(
                cell.getRowIndex(), cell.getRowIndex(), cell.getColumnIndex(), cell.getColumnIndex());
        // 数据有效性对象
        DataValidation data_validation = helper.createValidation(constraint, regions);
        // 加入sheet
        sheet.addValidationData(data_validation);
    }

    /**
     * 获得加锁单元格样式
     *
     * @param workbook
     * @param isHc
     *
     * @return
     */
    public static CellStyle getLockStyle(Workbook workbook, Object... isHc) {
        //  数字 靠右 锁定
        CellStyle lockStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints((short) 10);
        lockStyle.setFont(font);
        short color = 0x16;
        lockStyle.setFillForegroundColor(color);
        lockStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 设置边框
        Short black = 0x8;
        lockStyle.setBorderBottom(BorderStyle.THIN);
        lockStyle.setBottomBorderColor(black);
        lockStyle.setBorderLeft(BorderStyle.THIN);
        lockStyle.setLeftBorderColor(black);
        lockStyle.setBorderTop(BorderStyle.THIN);
        lockStyle.setTopBorderColor(black);
        lockStyle.setBorderRight(BorderStyle.THIN);
        lockStyle.setRightBorderColor(black);
        lockStyle.setLocked(true);
        lockStyle.setAlignment(HorizontalAlignment.CENTER);
        lockStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return lockStyle;
    }

    /**
     * 获得编辑单元格样式
     *
     * @param workbook
     *
     * @return
     */
    public static CellStyle getEditStyle(Workbook workbook) {
        //  数字 靠右 可编辑
        CellStyle editStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("微软雅黑");
        editStyle.setFont(font);

        // 设置边框
        Short black = 0x8;
        editStyle.setBorderBottom(BorderStyle.THIN);
        editStyle.setBottomBorderColor(black);
        editStyle.setBorderLeft(BorderStyle.THIN);
        editStyle.setLeftBorderColor(black);
        editStyle.setBorderTop(BorderStyle.THIN);
        editStyle.setTopBorderColor(black);
        editStyle.setBorderRight(BorderStyle.THIN);
        editStyle.setRightBorderColor(black);
        editStyle.setLocked(false);
        editStyle.setAlignment(HorizontalAlignment.RIGHT);
        editStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return editStyle;
    }

    /**
     * 获取数字数据格式
     *
     * @param workbook
     * @param thousands
     * @param pointIndex
     * @param autoFillZero
     * @param percent
     * @param negativeStyle
     *
     * @return
     */
    public static short getDataFormatIndex(Workbook workbook, boolean thousands,
                                           Integer pointIndex, boolean autoFillZero, boolean percent,
                                           Integer negativeStyle) {
        StringBuilder positiveNum = new StringBuilder();
        StringBuilder negativeNum = new StringBuilder();
        // 如果设置自定义格式则加载自定义格式
        if (thousands) {
            positiveNum.append("#,##");
            negativeNum.append("-#,##");
        } else {
            negativeNum.append("-");
        }
        // 小数点
        if (null != pointIndex) {
            positiveNum.append("0");
            negativeNum.append("0");
            String autoFillStr = autoFillZero ? "0" : "#";
            for (int i = 0; i < pointIndex; i++) {
                if (i == 0) {
                    positiveNum.append(".");
                    negativeNum.append(".");
                }
                positiveNum.append(autoFillStr);
                negativeNum.append(autoFillStr);
            }
        } else {
            positiveNum.append("0.################");
            negativeNum.append("0.################");
        }

        // 百分比
        if (percent) {
            positiveNum.append("%");
            negativeNum.append("%");
        }

        // 负数格式
        if (null != negativeStyle) {
            switch (negativeStyle) {
                case 1:
                    // 负数（红色）:-123
                    negativeNum.insert(0, "[Red]");
                    break;
                case 2:
                    // 括号:(123)
                    negativeNum = new StringBuilder("(" + negativeNum + ")");
                    break;
                case 3:
                    // 负数（红色）:(123)
                    negativeNum = new StringBuilder("[Red](" + negativeNum + ")");
                    break;
                default:
            }
        }
        DataFormat dataFormatNew = workbook.createDataFormat();
        short formatIndex = dataFormatNew.getFormat(positiveNum + "_);" + negativeNum);
        return formatIndex;
    }

    /**
     * 创建文档信息
     *
     * @param workbook0
     * @param manager
     * @param company
     * @param subject
     * @param title
     * @param author
     * @param comments
     */
    public static void createInformation(Workbook workbook0, String manager, String company, String subject,
                                         String title, String author, String comments) {
        if (workbook0 instanceof XSSFWorkbook) {
            HSSFWorkbook workbook = (HSSFWorkbook) workbook0;
            // 创建文档信息
            workbook.createInformationProperties();
            // 摘要信息
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();
            // 类别
            dsi.setCategory("Excel文件(xls)");
            // 管理者
            dsi.setManager(manager);
            // 公司
            dsi.setCompany(company);
            // 摘要信息
            SummaryInformation si = workbook.getSummaryInformation();
            // 主题
            si.setSubject(subject);
            // 标题
            si.setTitle(title);
            // 作者
            si.setAuthor(author);
            // 备注
            si.setComments(comments);
        } else {
            XSSFWorkbook workbook = (XSSFWorkbook) workbook0;
            // 暂无
        }
    }

    /**
     * 设置页眉信息
     *
     * @param sheet
     * @param left
     * @param center
     * @param right
     */
    public static void header2Sheet(Sheet sheet, String left, String center, String right) {
        // 得到页眉
        Header header = sheet.getHeader();
        header.setLeft(left);
        header.setCenter(center);
        header.setRight(right);
    }

    /**
     * 设置页脚信息
     *
     * @param sheet
     * @param left
     * @param center
     * @param right
     */
    public static void foot2Sheet(Sheet sheet, String left, String center, String right) {
        // 得到页脚
        Footer footer = sheet.getFooter();
        footer.setLeft(left);
        footer.setCenter(center);
        footer.setRight(right);
    }

    /**
     * 添加 url 链接到单元格
     *
     * @param cell
     * @param url
     */
    public static void hyperlink2Cell(Cell cell, String url) {
        CreationHelper createHelper = cell.getSheet().getWorkbook().getCreationHelper();
        // 关联到网站
        Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
        link.setAddress(url);
        cell.setHyperlink(link);
    }

    /**
     * 添加 邮件发送快捷键 到单元格
     *
     * @param cell
     * @param email
     * @param suject
     */
    public static void hyperlink2Cell(Cell cell, String email, String suject) {
        CreationHelper createHelper = cell.getSheet().getWorkbook().getCreationHelper();
        // 关联到网站
        Hyperlink link = createHelper.createHyperlink(HyperlinkType.EMAIL);
        link.setAddress("mailto:" + email + "?subject=" + suject);
        cell.setHyperlink(link);
    }

    /**
     * 添加 当前文件内 跳转 到 指定单元格 的链接到单元格
     *
     * @param cell
     * @param sheetName
     * @param col
     * @param row
     */
    public static void hyperlink2Cell(Cell cell, String sheetName, String col, String row) {
        CreationHelper createHelper = cell.getSheet().getWorkbook().getCreationHelper();
        // 关联到网站
        Hyperlink link = createHelper.createHyperlink(HyperlinkType.EMAIL);
        link.setAddress("'" + sheetName + "'!" + col + row);
        cell.setHyperlink(link);
    }

    /**
     * 打印设置
     *
     * @param sheet
     *
     * @return
     */
    public static PrintSetup printSetup(Sheet sheet) {
        // 得到打印对象
        PrintSetup print = sheet.getPrintSetup();
        // true，则表示页面方向为横向；否则为纵向
        print.setLandscape(false);
        // 缩放比例80%(设置为0-100之间的值)
        print.setScale((short) 80);
        // 设置页宽
        print.setFitWidth((short) 2);
        // 设置页高
        print.setFitHeight((short) 4);
        // 纸张设置
        print.setPaperSize(PrintSetup.A4_PAPERSIZE);
        // 设置打印起始页码不使用"自动"
        print.setUsePage(true);
        // 设置打印起始页码
        print.setPageStart((short) 6);
        // 设置打印网格线
        sheet.setPrintGridlines(true);
        // 值为true时，表示单色打印
        print.setNoColor(true);
        // 值为true时，表示用草稿品质打印
        print.setDraft(true);
        // true表示“先行后列”；false表示“先列后行”
        print.setLeftToRight(true);
        // 设置打印批注
        print.setNotes(true);
        // Sheet页自适应页面大小
        sheet.setAutobreaks(false);
        return print;
    }
}
