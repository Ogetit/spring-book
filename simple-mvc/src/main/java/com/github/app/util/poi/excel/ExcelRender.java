package com.github.app.util.poi.excel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import com.github.app.util.refect.ReflectionUtil;

/**
 * 文件描述 Excel文件导入导出工具
 *
 * @author ouyangjie
 * @Title: ExcelRender
 */
public class ExcelRender<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Class<T> clazz;

    private Map<Integer, Set<String>> pullDataMap;

    private Map<Integer, Integer> columnWidthMap;

    private Map<Integer, String> titleMap;

    private Set<Integer> uneditableColumns;

    private boolean xlsxFile = true;

    private int dataStartIndex = 1;

    private Font font;

    private Font markFont;

    private CellStyle titleCellStyle;

    private CellStyle titleMarkCellStyle;

    private CellStyle cellStyle;

    private CellStyle markCellStyle;

    private List<Field> fields;

    private List<String> errorMsgs;

    private boolean saveMutiDataError = false;

    public int getDataStartIndex() {
        return dataStartIndex;
    }

    public void setDataStartIndex(int dataStartIndex) {
        if (dataStartIndex < 1) {
            throw new IllegalArgumentException("数据开始行数Index不能小于1！");
        }
        this.dataStartIndex = dataStartIndex;
    }

    public boolean isXlsxFile() {
        return xlsxFile;
    }

    public void setXlsxFile(boolean xlsxFile) {
        this.xlsxFile = xlsxFile;
    }

    public Map<Integer, Set<String>> getPullDataMap() {
        return pullDataMap;
    }

    public void setPullDataMap(Map<Integer, Set<String>> pullDataMap) {
        this.pullDataMap = pullDataMap;
    }

    public void setColumnWidthMap(Map<Integer, Integer> columnWidthMap) {
        this.columnWidthMap = columnWidthMap;
    }

    public Map<Integer, String> getTitleMap() {
        return titleMap;
    }

    public void setTitleMap(Map<Integer, String> titleMap) {
        this.titleMap = titleMap;
    }

    public Set<Integer> getUneditableColumns() {
        return uneditableColumns;
    }

    public void setUneditableColumns(Set<Integer> uneditableColumns) {
        this.uneditableColumns = uneditableColumns;
    }

    public List<String> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(List<String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }

    public void setSaveMutiDataError(boolean saveMutiDataError) {
        this.saveMutiDataError = saveMutiDataError;
    }

    /**
     * 继承实现的时候自动取泛型，不用写构造函数，调用父类构造函数，直接默认调用构造函数
     */
    public ExcelRender() {
        this.clazz = ReflectionUtil.getSuperClassGenricType(this.getClass());
    }

    public ExcelRender(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 初始化一些基本属性
     * @param workbook
     */
    private void initBaseFields(Workbook workbook) {
        /* *********普通字体样式********* */
        this.font = workbook.createFont();
        this.font.setFontName("Arail narrow");
        this.font.setColor(Font.COLOR_NORMAL);
                /* *********标红字体样式********* */
        this.markFont = workbook.createFont();
        this.markFont.setFontName("Arail narrow");
        this.markFont.setColor(Font.COLOR_RED);

        // 表头属性定义
        // sheet.protectSheet("super");
        this.titleCellStyle = getLockStyle(workbook);
        this.titleMarkCellStyle = getLockStyle(workbook);
        this.titleMarkCellStyle.setFont(this.markFont);

        /* *************创建内容列*************** */
        this.cellStyle = getEditStyle(workbook);
        this.markCellStyle = getEditStyle(workbook);
        this.markCellStyle.setFont(markFont);

        // 得到所有定义字段
        Field[] allFields = clazz.getDeclaredFields();
        if (null != clazz.getSuperclass()) {
            allFields = ArrayUtils.addAll(allFields, clazz.getSuperclass().getDeclaredFields());
        }
        this.fields = new ArrayList<Field>();
        // 得到所有field并存放到一个list中
        for (Field field : allFields) {
            if (field.isAnnotationPresent(Excel.class)) {
                this.fields.add(field);
            }
        }
    }

    /**
     * 将excel表单数据源的数据导入到list
     *
     * @param sheetName 工作表的名称
     * @param input     java输入流
     */
    public List<T> excelToList(String sheetName, InputStream input) {
        List<T> list = new ArrayList<T>();
        try {
            Workbook book = createWorkbook(input);
            Sheet sheet = null;
            // 如果指定sheet名,则取指定sheet中的内容.
            if (StringUtils.isNotBlank(sheetName)) {
                sheet = book.getSheet(sheetName);
            }
            // 如果传入的sheet名不存在则默认指向第1个sheet.
            if (sheet == null) {
                sheet = book.getSheetAt(0);
            }
            // 得到数据的行数
            int rows = sheet.getLastRowNum();
            // 有数据时才处理
            if (rows >= this.dataStartIndex) {
                // 清空错误信息
                this.emptyErrorMsgs();
                // 得到类的所有field
                Map<Integer, Field> fieldsMap = getColIndexFieldMap();
                // 从第 this.dataStartIndex+1 行开始取数据,默认第一行是表头
                for (int i = this.dataStartIndex, len = rows; i <= len; i++) {
                    // 得到一行中的所有单元格对象.
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    Iterator<Cell> cells = row.cellIterator();
                    T entity = null;
                    while (cells.hasNext()) {
                        // 单元格中的内容.
                        Cell cell = cells.next();

                        // 如果不存在实例则新建
                        entity = (entity == null ? this.clazz.newInstance() : entity);
                        // 从map中得到对应列的field
                        Field field = fieldsMap.get(cell.getColumnIndex());
                        if (field == null) {
                            continue;
                        }
                        Excel attr = field.getAnnotation(Excel.class);
                        String value = getCellValue(cell, attr.readShowValue());

                        try {
                            fillValueEntity(entity, field, value);
                        } catch (Exception e) {
                            String columnIndexStr = getExcelColIndexStr(cell.getColumnIndex());
                            String msg = "第【" + (i + 1) + "】行，第【" + columnIndexStr + "】列，数据转化异常！";
                            // 是否保存多个数据异常信息
                            if (this.saveMutiDataError) {
                                this.errorMsgs.add(msg);
                            } else {
                                // 即时抛出异常信息
                                throw new IllegalArgumentException(msg, e);
                            }
                        }
                    }
                    if (entity != null) {
                        list.add(entity);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("导入异常：" + e.getMessage(), e);
        }
        return list;
    }

    private Map<Integer, Field> getColIndexFieldMap() {
        Field[] allFields = clazz.getDeclaredFields();
        if (null != clazz.getSuperclass()) {
            allFields = ArrayUtils.addAll(allFields, clazz.getSuperclass().getDeclaredFields());
        }
        // 定义一个map用于存放列的序号和field
        Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();
        for (int i = 0, index = 0; i < allFields.length; i++) {
            Field field = allFields[i];
            // 将有注解的field存放到map中
            if (field.isAnnotationPresent(Excel.class)) {
                // 设置类的私有字段属性可访问
                field.setAccessible(true);
                Excel attr = field.getAnnotation(Excel.class);
                // 根据指定的顺序获得列号
                if (StringUtils.isNotBlank(attr.column())) {
                    int col = getExcelColIndex(attr.column());
                    fieldsMap.put(col, field);
                } else {
                    fieldsMap.put(index, field);
                }
                index++;
            }
        }
        return fieldsMap;
    }

    /**
     * 清空错误信息
     */
    private void emptyErrorMsgs() {
        if (null == this.errorMsgs) {
            this.errorMsgs = new ArrayList<String>();
        } else {
            this.errorMsgs.clear();
        }
    }

    /**
     * 填充值到实体里
     * @param entity
     * @param field
     * @param value
     * @throws IllegalAccessException
     * @throws ParseException
     */
    private void fillValueEntity(T entity, Field field, String value) throws IllegalAccessException, ParseException {
        // 取得类型,并根据对象类型设置值.
        Class<?> fieldType = field.getType();
        if (StringUtils.isBlank(value) || value.indexOf("合计：") != -1) {
            return;
        }
        if (String.class == fieldType) {
            field.set(entity, String.valueOf(value));
        } else if (BigDecimal.class == fieldType) {
            value = value.indexOf("%") != -1 ? value.replace("%", "") : value;
            field.set(entity, BigDecimal.valueOf(Double.valueOf(value)));
        } else if (Date.class == fieldType) {
            field.set(entity, DateUtils.parseDate(value, new String[] {"yyyy-MM-dd", "yyyy/MM/dd"}));
        } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
            field.set(entity, Integer.parseInt(value));
        } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
            field.set(entity, Long.valueOf(value));
        } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
            field.set(entity, Float.valueOf(value));
        } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
            field.set(entity, Short.valueOf(value));
        } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
            field.set(entity, Double.valueOf(value));
        } else if (Character.TYPE == fieldType) {
            if ((value != null) && (value.length() > 0)) {
                field.set(entity, Character.valueOf(value.charAt(0)));
            }
        }
    }

    /**
     * 将list数据源的数据导入到excel表单
     *
     * @param list      数据源
     * @param sheetName 工作表的名称
     * @param output    java输出流
     */
    public boolean listToExcel(List<T> list, String sheetName, OutputStream output) {
        try {
            // excel中每个sheet中最多有65536行
            int sheetSize = 65536;
            // 产生工作薄对象
            Workbook workbook = createWorkbook(null);
            // 初始化基础样式和变量
            this.initBaseFields(workbook);
            // 取出一共有多少个sheet
            int listSize = CollectionUtils.isEmpty(list) ? 0 : list.size();
            double sheetNo = Math.ceil(listSize / sheetSize);

            for (int index = 0; index <= sheetNo; index++) {
                // 产生工作表对象
                Sheet sheet = workbook.createSheet();
                if (sheetNo > 0) {
                    // 设置工作表的名称，拆分 sheet 存入
                    workbook.setSheetName(index, sheetName + index);
                } else {
                    // 设置工作表的名称.
                    workbook.setSheetName(index, sheetName);
                }

                // 通常第一页会表头上部会定义一些额外信息，该方法可以在子类覆写
                if (index == 0) {
                    this.extraDataInfo2Sheet(sheet);
                }

                // 表头填充
                this.tableTitle2Sheet(sheet);

                // 表格数据填充 （数据量超标要分多个 sheet 放入）
                int startNo = index * sheetSize;
                int endNo = Math.min(startNo + sheetSize, listSize);
                this.tableData2Sheet(sheet, list, startNo, endNo);

                // 合计 该 sheet 的列 （设置了合计属性才合计）
                this.sumColumn2Sheet(sheet);
            }

            // 添加其他的东西到 sheet
            addOthers2Workbook(workbook, sheetNo);

            output.flush();
            workbook.write(output);
            output.close();
            return Boolean.TRUE;
        } catch (Exception e) {
            throw new IllegalArgumentException("导出excel异常!", e);
        }
    }

    /**
     * 在表格信息前（通常上部）加些额外信息，各业务子类根据自身需求重写
     * @param sheet
     */
    public void extraDataInfo2Sheet(Sheet sheet) {}

    /**
     * 添加其他的东西到 工作簿
     * @param workbook
     * @param dataSheetNo
     */
    private void addOthers2Workbook(Workbook workbook, double dataSheetNo) {
        // 添加下拉框 数据域
        if (!CollectionUtils.isEmpty(this.pullDataMap)) {
            // 添加 下拉框
            int selectIndex = (int) (dataSheetNo + 1);
            Sheet selectSheet = workbook.createSheet();
            workbook.setSheetName(selectIndex, "大于255下拉框值域");
            int startRow = this.dataStartIndex;
            int endRow = this.dataStartIndex + 1000;
            for (int index = 0; index <= dataSheetNo; index++) {
                Sheet sheet = workbook.getSheetAt(index);
                addPullDownSelect(sheet, selectSheet, startRow, endRow, this.pullDataMap);
            }
            workbook.setSheetHidden(selectIndex, true);
        }
    }

    /**
     * 合计列
     * @param sheet
     */
    private void sumColumn2Sheet(Sheet sheet) {
        /* *************创建合计列*************** */
        Row lastRow = sheet.createRow((short) (sheet.getLastRowNum() + 1));
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            Excel attr = field.getAnnotation(Excel.class);
            if (attr.isSum()) {
                int col = i;
                // 根据指定的顺序获得列号
                if (StringUtils.isNotBlank(attr.column())) {
                    col = getExcelColIndex(attr.column());
                }
                BigDecimal totalNumber = BigDecimal.ZERO;
                for (int j = 1, len = (sheet.getLastRowNum() - 1); j < len; j++) {
                    Row hssfRow = sheet.getRow(j);
                    if (hssfRow != null) {
                        Cell hssfCell = hssfRow.getCell(col);
                        if (hssfCell != null && hssfCell.getCellType() == CellType.STRING
                                && NumberUtils.isNumber(hssfCell.getStringCellValue())) {
                            totalNumber = totalNumber.add(new BigDecimal(hssfCell.getStringCellValue()));
                        }
                    }
                }
                Cell sumCell = lastRow.createCell(col);
                sumCell.setCellValue("合计：" + totalNumber);
            }
        }
    }

    /**
     * 数据填充
     * @param sheet
     * @param dataList
     * @param startNo
     * @param endNo
     * @throws IllegalAccessException
     */
    private void tableData2Sheet(Sheet sheet, List<T> dataList, int startNo, int endNo)
            throws IllegalAccessException {
        Row row;
        Cell cell;
        // 写入各条记录,每条记录对应 excel 表中的一行
        for (int i = startNo; i < endNo; i++) {
            row = sheet.createRow(i + this.dataStartIndex - startNo);
            if (null == dataList) {
                break;
            }
            // 得到导出对象.
            T vo = dataList.get(i);
            if (null == vo) {
                continue;
            }
            for (int j = 0; j < this.fields.size(); j++) {
                // 获得 field
                Field field = this.fields.get(j);
                // 设置实体类私有属性可访问
                field.setAccessible(true);
                Excel attr = field.getAnnotation(Excel.class);
                int col = j;
                // 根据指定的顺序获得列号
                if (StringUtils.isNotBlank(attr.column())) {
                    col = getExcelColIndex(attr.column());
                }
                // 根据 ExcelVOAttribute 中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                if (attr.isExport()) {
                    // 创建cell
                    cell = row.createCell(col);
                    cell.setCellType(CellType.STRING);
                    if (null != uneditableColumns && uneditableColumns.contains(col)) {
                        setDataLockValidation(sheet, cell);
                    }
                    if (attr.isMark()) {
                        cell.setCellStyle(this.markCellStyle);
                    } else {
                        cell.setCellStyle(this.cellStyle);
                    }
                    // 如果数据存在就填入,不存在填入空格
                    Class<?> classType = (Class<?>) field.getType();
                    String value = null;
                    if (field.get(vo) != null && classType.isAssignableFrom(Date.class)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        value = sdf.format(field.get(vo));
                    }
                    if (field.get(vo) == null) {
                        cell.setCellValue("");
                    } else if (value == null) {
                        cell.setCellValue(String.valueOf(field.get(vo)));
                    } else {
                        cell.setCellValue(value);
                    }
                }
            }
        }
    }

    /**
     * 表头填充
     * @param sheet
     */
    private void tableTitle2Sheet(Sheet sheet) {
        Row row;
        Cell cell;
        // 产生单元格 产生一行
        row = sheet.createRow(this.dataStartIndex - 1);
                /* *************创建列头名称*************** */
        for (int i = 0; i < this.fields.size(); i++) {
            Field field = this.fields.get(i);
            Excel attr = field.getAnnotation(Excel.class);
            int col = i;
            // 根据指定的顺序获得列号
            if (StringUtils.isNotBlank(attr.column())) {
                col = getExcelColIndex(attr.column());
            }
            // 创建列
            cell = row.createCell(col);
            // 对某个单元格加锁
            setDataLockValidation(sheet, cell);
            if (attr.isMark()) {
                cell.setCellStyle(this.titleMarkCellStyle);
            } else {
                cell.setCellStyle(this.titleCellStyle);
            }

            // 设置列宽
            int columnWidth;
            if (null != this.columnWidthMap && null != this.columnWidthMap.get(col)) {
                columnWidth = this.columnWidthMap.get(col);
            } else {
                int titleBytes = (attr.name().getBytes().length <= 4 ? 6 : attr.name().getBytes().length);
                columnWidth = (int) (titleBytes * 1.5 * 256);
            }
            sheet.setColumnWidth(col, columnWidth);

            // 设置列中写入内容为 String 类型
            cell.setCellType(CellType.STRING);

            // 写入列名
            if (null != this.titleMap && null != this.titleMap.get(col)) {
                cell.setCellValue(titleMap.get(col));
            } else {
                cell.setCellValue(attr.name());
            }

            // 如果设置了提示信息则鼠标放上去提示.
            if (StringUtils.isNotBlank(attr.prompt())) {
                setPrompt(sheet, "", attr.prompt(), 1, 100, col, col);
            }

            // 如果设置了 combo 属性则本列只能选择不能输入
            if (attr.readonlyCols().length > 0) {
                setValidation(sheet, attr.readonlyCols(), 1, 100, col, col);
            }
        }
    }

    /**
     * 创建 Workbook
     * @param input
     * @return
     * @throws IOException
     */
    protected Workbook createWorkbook(InputStream input) throws IOException {
        Workbook book;
        if (this.xlsxFile) {
            book = null != input ? new XSSFWorkbook(input) : new XSSFWorkbook();
        } else {
            book = null != input ? new HSSFWorkbook(input) : new HSSFWorkbook();
        }
        return book;
    }

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
     * 添加 下拉框 数据
     *
     * @param sheet
     * @param pullDataMap
     */
    public static void addPullDownSelect(Sheet sheet, Sheet selectSheet, int startRow, int endRow, Map<Integer,
            Set<String>> pullDataMap) {
        if (CollectionUtils.isEmpty(pullDataMap)) {
            return;
        }
        int index = 0;
        for (Map.Entry<Integer, Set<String>> entry : pullDataMap.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            String[] data = new String[entry.getValue().size()];
            entry.getValue().toArray(data);

            if (data.length < 5) {
                setPullDown(sheet, data, startRow, endRow, entry.getKey(), entry.getKey());
            } else {
                setPullDownMax(index, sheet, selectSheet, data, startRow, endRow, entry.getKey(), entry.getKey());
                index++;
            }
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
        // Sheet2第A1到A5000作为下拉列表来源数据
        String strFormula = sheet.getSheetName() + "!$" + arr[index] + "$1:$" + arr[index] + "$5000";
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
     * @param workbook
     * @param isHc
     * @return
     */
    public static CellStyle getLockStyle(Workbook workbook, Object... isHc) {
        //  数字 靠右 锁定
        CellStyle lockStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
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
     * @param workbook
     * @param isHc
     * @return
     */
    public static CellStyle getEditStyle(Workbook workbook, Object... isHc) {
        //  数字 靠右 可编辑
        CellStyle editStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        editStyle.setFont(font);
        editStyle.setLocked(false);
        editStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        if (isHc.length > 0) {
            editStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        }
        editStyle.setAlignment(HorizontalAlignment.RIGHT);
        editStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return editStyle;
    }
}
