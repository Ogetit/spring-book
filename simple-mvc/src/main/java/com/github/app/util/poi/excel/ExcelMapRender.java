package com.github.app.util.poi.excel;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;

import com.github.app.util.poi.excel.bean.ExcelErrorDomain;
import com.github.app.util.poi.excel.bean.ExcelItem;
import com.github.app.util.poi.excel.util.ExcelUtil;

/**
 * 文件描述 Excel文件导入导出工具
 *
 * @author ouyangjie
 * @Title: ExcelMapRender
 * @ProjectName ifs-marketing
 * @date 2019/9/19 7:29 PM
 */
public class ExcelMapRender implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<Integer, Set<String>> pullDataMap;

    private Map<Integer, Integer> columnWidthMap;

    private Map<Integer, String> titleMap;

    private Map<Integer, Integer> lengthLimitMap;

    private Set<Integer> uneditableColumns;

    private Set<String> markedIndexMap;

    private boolean xlsxFile = true;

    private int dataStartIndex = 1;

    private Font font;

    private Font markFont;

    private CellStyle titleCellStyle;

    private CellStyle titleMarkCellStyle;

    private CellStyle cellStyle;

    private CellStyle markCellStyle;

    private CellStyle numberCellStyle;

    private CellStyle markNumberCellStyle;

    private CellStyle numberPercentCellStyle;

    private CellStyle markNumberPercentCellStyle;

    private Map<String, CellStyle> formatCellStyleMap;

    private List<ExcelErrorDomain> errorMsgs;

    List<ExcelItem> fields;

    private boolean saveMutiDataError = false;

    private int pullDownColIndex = 0;

    private String currSheetName;

    private boolean displayGridlines = true;

    public ExcelMapRender(List<ExcelItem> fields) {
        this.fields = fields;
    }

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

    public Set<String> getMarkedIndexMap() {
        return markedIndexMap;
    }

    public void setMarkedIndexMap(Set<String> markedIndexMap) {
        this.markedIndexMap = markedIndexMap;
    }

    public List<ExcelErrorDomain> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(List<ExcelErrorDomain> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }

    public void setSaveMutiDataError(boolean saveMutiDataError) {
        this.saveMutiDataError = saveMutiDataError;
    }

    public String getCurrSheetName() {
        return currSheetName;
    }

    public CellStyle getTitleCellStyle() {
        return titleCellStyle;
    }

    public CellStyle getTitleMarkCellStyle() {
        return titleMarkCellStyle;
    }

    public CellStyle getDefaultCellStyle() {
        return cellStyle;
    }

    public void setDisplayGridlines(boolean displayGridlines) {
        this.displayGridlines = displayGridlines;
    }

    public boolean isDisplayGridlines() {
        return displayGridlines;
    }

    /**
     * 初始化一些基本属性
     *
     * @param workbook
     */
    private void initBaseFields(Workbook workbook) {
        /* *********普通字体样式********* */
        this.font = workbook.createFont();
        this.font.setFontName("Arail narrow");
        this.font.setColor(Font.COLOR_NORMAL);
        /* *********标红字体样式********* */
        this.markFont = workbook.createFont();
        this.markFont.setFontHeightInPoints((short) 10);
        this.markFont.setFontName("Arail narrow");
        this.markFont.setColor(Font.COLOR_RED);

        // 表头属性定义
        // sheet.protectSheet("super");
        this.titleCellStyle = ExcelUtil.getLockStyle(workbook);
        this.titleMarkCellStyle = ExcelUtil.getLockStyle(workbook);
        this.titleMarkCellStyle.setFont(this.markFont);
        this.titleMarkCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

        /* *************创建内容列*************** */
        this.cellStyle = ExcelUtil.getEditStyle(workbook);
        this.markCellStyle = ExcelUtil.getEditStyle(workbook);
        this.markCellStyle.setFont(markFont);

        if (null == this.lengthLimitMap) {
            this.lengthLimitMap = new HashMap<Integer, Integer>();
        } else {
            this.lengthLimitMap.clear();
        }
        pullDownColIndex = 0;

        numberCellStyle = workbook.createCellStyle();
        numberCellStyle.cloneStyleFrom(cellStyle);
        short dataFormatIndex = ExcelUtil.getDataFormatIndex(workbook, true, 2, true, false, null);
        numberCellStyle.setDataFormat(dataFormatIndex);
        numberCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        markNumberCellStyle = workbook.createCellStyle();
        markNumberCellStyle.cloneStyleFrom(numberCellStyle);
        markNumberCellStyle.setFont(markFont);

        numberPercentCellStyle = workbook.createCellStyle();
        numberPercentCellStyle.cloneStyleFrom(cellStyle);
        short dataPercentCellFormatIndex = ExcelUtil.getDataFormatIndex(workbook, true, 2, true, true, null);
        numberPercentCellStyle.setDataFormat(dataPercentCellFormatIndex);
        numberPercentCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        markNumberPercentCellStyle = workbook.createCellStyle();
        markNumberPercentCellStyle.cloneStyleFrom(numberPercentCellStyle);
        markNumberPercentCellStyle.setFont(markFont);

        formatCellStyleMap = new HashMap<String, CellStyle>();
    }

    /**
     * 将excel表单数据源的数据导入到list
     *
     * @param sheetName 工作表的名称
     * @param input     java输入流
     */
    public List<Map> excelToList(String sheetName, InputStream input) {
        List<Map> list = new ArrayList<Map>();
        try {
            Workbook book = ExcelUtil.createWorkbook(input, this.xlsxFile);
            Sheet sheet = null;
            // 如果指定sheet名,则取指定sheet中的内容.
            if (StringUtils.isNotBlank(sheetName)) {
                sheet = book.getSheet(sheetName);
            }
            // 如果传入的sheet名不存在则默认指向第1个sheet.
            if (sheet == null) {
                sheet = book.getSheetAt(0);
            }
            currSheetName = sheet.getSheetName();
            this.extraDataInfoFromSheet(sheet);
            // 得到数据的行数
            int rows = sheet.getLastRowNum();
            // 有数据时才处理
            if (rows >= this.dataStartIndex) {
                // 清空错误信息
                this.emptyErrorMsgs();
                // 得到类的所有field
                Map<Integer, ExcelItem> fieldsMap = getColIndexFieldMap();
                // 从第 this.dataStartIndex+1 行开始取数据,默认第一行是表头
                for (int i = this.dataStartIndex, len = rows; i <= len; i++) {
                    // 得到一行中的所有单元格对象.
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    Iterator<Cell> cells = row.cellIterator();
                    Map entity = new HashMap();
                    while (cells.hasNext()) {
                        // 单元格中的内容.
                        Cell cell = cells.next();

                        // 从map中得到对应列的field
                        ExcelItem field = fieldsMap.get(cell.getColumnIndex());
                        if (field == null) {
                            continue;
                        }
                        // 如果忽略数据的读取，则跳过
                        if (field.isIgnoreDataFromExcel()) {
                            continue;
                        }

                        String value = ExcelUtil.getCellValue(cell, field.isReadShowValue());

                        try {
                            fillValueEntity(entity, field, value);
                        } catch (IllegalArgumentException e) {
                            String columnIndexStr = ExcelUtil.getExcelColIndexStr(cell.getColumnIndex());
                            String rowIndexStr = "" + (i + 1);
                            errorExceptionLoging(rowIndexStr, columnIndexStr, e);
                        } catch (Exception e) {
                            String columnIndexStr = ExcelUtil.getExcelColIndexStr(cell.getColumnIndex());
                            String rowIndexStr = "" + (i + 1);
                            errorExceptionLoging(rowIndexStr, columnIndexStr, e);
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

    /**
     * 读取在表格信息前（通常上部）额外信息，各业务子类根据自身需求读取
     *
     * @param sheet
     */
    public void extraDataInfoFromSheet(Sheet sheet) {

    }

    /**
     * 错误日志处理
     *
     * @param rowIndexStr
     * @param columnIndexStr
     * @param e
     */
    public void errorExceptionLoging(String rowIndexStr, String columnIndexStr, Exception e) {
        String eMsg = e instanceof IllegalArgumentException ? e.getMessage() : "";
        ExcelErrorDomain error = new ExcelErrorDomain(currSheetName, rowIndexStr, columnIndexStr, eMsg);
        // 是否保存多个数据异常信息
        if (this.saveMutiDataError) {
            this.errorMsgs.add(error);
        } else {
            // 即时抛出异常信息
            e.printStackTrace();
            String msg = "第【" + rowIndexStr + "】行，第【" + columnIndexStr + "】列，数据转化异常【" + eMsg + "】！";
            throw new IllegalArgumentException(msg, e);
        }
    }

    private Map<Integer, ExcelItem> getColIndexFieldMap() {
        // 定义一个map用于存放列的序号和field
        Map<Integer, ExcelItem> fieldsMap = new HashMap<Integer, ExcelItem>();
        if (CollectionUtils.isEmpty(this.fields)) {
            return fieldsMap;
        }
        int index = 0;
        for (ExcelItem field : this.fields) {
            // 根据指定的顺序获得列号
            if (field.getColumnIndex() < 0 && StringUtils.isNotBlank(field.getColumn())) {
                int col = ExcelUtil.getExcelColIndex(field.getColumn());
                field.setColumnIndex(col);
                fieldsMap.put(col, field);
            } else {
                fieldsMap.put(index, field);
            }
            index++;
        }

        return fieldsMap;
    }

    /**
     * 清空错误信息
     */
    private void emptyErrorMsgs() {
        if (null == this.errorMsgs) {
            this.errorMsgs = new ArrayList<ExcelErrorDomain>();
        } else {
            this.errorMsgs.clear();
        }
    }

    /**
     * 填充值到实体里
     *
     * @param entity
     * @param field
     * @param value
     *
     * @throws IllegalAccessException
     * @throws ParseException
     */
    private void fillValueEntity(Map entity, ExcelItem field, String value)
            throws IllegalAccessException, ParseException {
        // 取得类型,并根据对象类型设置值.
        Class<?> fieldType = field.getType();
        if (StringUtils.isBlank(value) || value.indexOf("合计：") != -1) {
            return;
        }
        if (String.class == fieldType) {
            if (field.getByteLength() > 0 && value.getBytes().length > field.getByteLength()) {
                throw new IllegalArgumentException("[" + field.getName() + "]的长度不满足：" + field.getPrompt());
            }
            entity.put(field.getFieldName(), String.valueOf(value));
        } else if (BigDecimal.class == fieldType) {
            value = value.indexOf("%") != -1 ? value.replace("%", "") : value;
            entity.put(field.getFieldName(), BigDecimal.valueOf(Double.valueOf(value)));
        } else if (Date.class == fieldType) {
            entity.put(field.getFieldName(), DateUtils.parseDate(value, new String[] {"yyyy-MM-dd", "yyyy/MM/dd"}));
        } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
            entity.put(field.getFieldName(), Integer.parseInt(value));
        } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
            entity.put(field.getFieldName(), Long.valueOf(value));
        } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
            entity.put(field.getFieldName(), Float.valueOf(value));
        } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
            entity.put(field.getFieldName(), Short.valueOf(value));
        } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
            entity.put(field.getFieldName(), Double.valueOf(value));
        } else if (Character.TYPE == fieldType && value != null && value.length() > 0) {
            entity.put(field.getFieldName(), Character.valueOf(value.charAt(0)));
        } else {
            entity.put(field.getFieldName(), value);
        }
    }

    /**
     * 将list数据源的数据导入到excel表单
     *
     * @param list      数据源
     * @param sheetName 工作表的名称
     * @param output    java输出流
     */
    public boolean listToExcel(List<Map> list, String sheetName, OutputStream output) {
        try {
            // excel中每个sheet中最多有65536行
            int sheetSize = 65536;
            // 产生工作薄对象
            Workbook workbook = ExcelUtil.createWorkbook(null, this.xlsxFile);
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
                    this.beforExtraDataInfo2Sheet(sheet);
                }

                // 表头填充
                this.tableTitle2Sheet(sheet);

                // 表格数据填充 （数据量超标要分多个 sheet 放入）
                int startNo = index * sheetSize;
                int endNo = Math.min(startNo + sheetSize, listSize);
                this.tableData2Sheet(sheet, list, startNo, endNo);

                // 合计 该 sheet 的列 （设置了合计属性才合计）
                this.sumColumn2Sheet(sheet);

                // 网格线隐藏与否
                sheet.setDisplayGridlines(this.displayGridlines);
            }

            // 添加其他的东西到 sheet
            this.addOthers2Workbook(workbook, sheetNo);
            // 其他额外的操作
            this.afterExtraInfo2Workbook(workbook);

            output.flush();
            workbook.write(output);
            output.close();
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("导出excel异常!", e);
        }
    }

    /**
     * 在表格信息前（通常上部）加些额外信息，各业务子类根据自身需求重写
     *
     * @param sheet
     */
    public void beforExtraDataInfo2Sheet(Sheet sheet) {
    }

    /**
     * 额外的信息添加到 Workbook
     *
     * @param workbook
     */
    public void afterExtraInfo2Workbook(Workbook workbook) {
    }

    /**
     * 添加其他的东西到 工作簿
     *
     * @param workbook
     * @param dataSheetNo
     */
    private void addOthers2Workbook(Workbook workbook, double dataSheetNo) {
        // 添加下拉框 数据域
        if (!CollectionUtils.isEmpty(this.pullDataMap)) {
            // 添加 下拉框
            Sheet selectSheet = getOrCreateSelectSheet(workbook);
            int startRow = this.dataStartIndex;
            int endRow = this.dataStartIndex + 1000;
            for (int index = 0; index <= dataSheetNo; index++) {
                Sheet sheet = workbook.getSheetAt(index);
                addPullDownSelect(sheet, selectSheet, startRow, endRow, this.pullDataMap);
            }
            int selectIndex = workbook.getSheetIndex(selectSheet.getSheetName());
            workbook.setSheetHidden(selectIndex, true);
        }
        // 添加单元格字数限制
        if (!CollectionUtils.isEmpty(this.lengthLimitMap)) {
            int startRow = this.dataStartIndex;
            int endRow = this.dataStartIndex + 1000;
            for (int index = 0; index <= dataSheetNo; index++) {
                Sheet sheet = workbook.getSheetAt(index);
                ExcelUtil.addStrLengthLimit(sheet, startRow, endRow, this.lengthLimitMap);
            }
        }
    }

    /**
     * 添加 下拉框 数据 批量添加到下拉框
     *
     * @param sheet
     * @param pullDataMap
     */
    public void addPullDownSelect(Sheet sheet, Sheet selectSheet, int startRow, int endRow, Map<Integer,
            Set<String>> pullDataMap) {
        if (CollectionUtils.isEmpty(pullDataMap)) {
            return;
        }
        for (Map.Entry<Integer, Set<String>> entry : pullDataMap.entrySet()) {
            addPullDownSelect(sheet, selectSheet, startRow, endRow, entry.getKey(), entry.getKey(), entry.getValue());
        }
    }

    /**
     * 添加 下拉框
     *
     * @param sheet
     * @param selectSheet
     * @param startRow
     * @param endRow
     * @param startCol
     * @param endCol
     * @param dataSet
     */
    public void addPullDownSelect(Sheet sheet, Sheet selectSheet, int startRow, int endRow, int startCol, int endCol,
                                  Set<String> dataSet) {
        if (CollectionUtils.isEmpty(dataSet)) {
            return;
        }
        String[] data = new String[dataSet.size()];
        dataSet.toArray(data);

        if (data.length < 5) {
            ExcelUtil.setPullDown(sheet, data, startRow, endRow, startCol, endCol);
        } else {
            ExcelUtil
                    .setPullDownMax(pullDownColIndex, sheet, selectSheet, data, startRow, endRow, startCol, endCol);
            pullDownColIndex++;
        }
    }

    /**
     * 获取或创建 SelectSheet
     *
     * @param workbook
     *
     * @return
     */
    protected Sheet getOrCreateSelectSheet(Workbook workbook) {
        String selectSheetName = "大于255下拉框值域";
        Sheet selectSheet = workbook.getSheet(selectSheetName);
        if (null == selectSheet) {
            selectSheet = workbook.createSheet(selectSheetName);
        }
        return selectSheet;
    }

    /**
     * 合计列
     *
     * @param sheet
     */
    private void sumColumn2Sheet(Sheet sheet) {
        /* *************创建合计列*************** */
        Row lastRow = sheet.createRow((short) (sheet.getLastRowNum() + 1));
        for (int i = 0; i < fields.size(); i++) {
            ExcelItem attr = fields.get(i);
            if (attr.isSum()) {
                int col = i;
                // 根据指定的顺序获得列号
                if (attr.getColumnIndex() < 0 && StringUtils.isNotBlank(attr.getColumn())) {
                    col = ExcelUtil.getExcelColIndex(attr.getColumn());
                    attr.setColumnIndex(col);
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
     *
     * @param sheet
     * @param dataList
     * @param startNo
     * @param endNo
     *
     * @throws IllegalAccessException
     */
    private void tableData2Sheet(Sheet sheet, List<Map> dataList, int startNo, int endNo)
            throws IllegalAccessException {
        Row row;
        Cell cell;
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        int dataMaxIndex = dataList.size() - 1;
        // 写入各条记录,每条记录对应 excel 表中的一行
        for (int i = startNo; i < endNo; i++) {
            int rowIndex = i + this.dataStartIndex;
            row = sheet.createRow(rowIndex - startNo);
            if (dataMaxIndex < i) {
                break;
            }
            // 得到导出对象.
            Map vo = dataList.get(i);
            if (null == vo) {
                continue;
            }
            for (int j = 0; j < this.fields.size(); j++) {
                // 获得 field
                ExcelItem field = this.fields.get(j);
                int col = j;
                // 根据指定的顺序获得列号
                if (field.getColumnIndex() < 0 && StringUtils.isNotBlank(field.getColumn())) {
                    col = ExcelUtil.getExcelColIndex(field.getColumn());
                    field.setColumnIndex(col);
                }
                // 根据 ExcelVOAttribute 中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                if (field.isExport()) {
                    // 创建cell
                    cell = row.createCell(col);
                    cell.setCellType(CellType.STRING);

                    // 单元格值填充
                    getAndFillValue2Cell(cell, vo, field);

                    // 填充导出数据的样式
                    fillCellStyle(cell, field, rowIndex, col);
                }
            }
        }
    }

    private void fillCellStyle(Cell cell, ExcelItem attr, int rowIndex, int colIndex) {
        Sheet sheet = cell.getSheet();
        if (null != uneditableColumns && uneditableColumns.contains(colIndex)) {
            ExcelUtil.setDataLockValidation(sheet, cell);
        }
        boolean isMarked = attr.isMark()
                || (null != markedIndexMap && markedIndexMap.contains(rowIndex + "_" + colIndex));
        if (isMarked) {
            if (attr.isNumber()) {
                cell.setCellStyle(this.markNumberCellStyle);
            } else if (attr.isPercentNumber()) {
                cell.setCellStyle(this.markNumberPercentCellStyle);
            } else {
                cell.setCellStyle(this.markCellStyle);
            }
        } else if (attr.isPercentNumber()) {
            cell.setCellStyle(this.numberPercentCellStyle);
        } else if (attr.isNumber()) {
            cell.setCellStyle(this.numberCellStyle);
        } else {
            cell.setCellStyle(this.cellStyle);
        }
        if (StringUtils.isNotBlank(attr.getDateFormat())) {
            CellStyle style = formatCellStyleMap.get(attr.getDateFormat());
            if (null == style) {
                style = sheet.getWorkbook().createCellStyle();
                // clone 此前定义的样式
                style.cloneStyleFrom(cell.getCellStyle());
                style.setDataFormat(cell.getSheet().getWorkbook().createDataFormat().getFormat(attr.getDateFormat()));
                formatCellStyleMap.put(attr.getDateFormat(), style);
            }
            cell.setCellStyle(style);
        }
    }

    /**
     * 获取 对象对应属性的 StrValue
     *
     * @param cell
     * @param vo
     * @param field
     *
     * @return
     *
     * @throws IllegalAccessException
     */
    private Object getAndFillValue2Cell(Cell cell, Map vo, ExcelItem field) throws IllegalAccessException {
        if (null == field) {
            return null;
        }
        Class<?> classType = field.getType();
        Object feildValue = vo.get(field.getFieldName());
        if (feildValue == null) {
            // 如果数据存在就填入,不存在填入空格
            cell.setCellValue("");
        } else if (classType.isAssignableFrom(Date.class)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            cell.setCellValue(sdf.format(feildValue));
        } else if (classType.isAssignableFrom(Double.class)) {
            cell.setCellValue((Double) feildValue);
            cell.setCellType(CellType.NUMERIC);
        } else if (classType.isAssignableFrom(BigDecimal.class)) {
            cell.setCellValue(((BigDecimal) feildValue).doubleValue());
            cell.setCellType(CellType.NUMERIC);
        } else {
            cell.setCellValue(String.valueOf(feildValue));
        }

        return feildValue;
    }

    /**
     * 表头填充
     *
     * @param sheet
     */
    private void tableTitle2Sheet(Sheet sheet) {
        Row row;
        Cell cell;
        // 产生单元格 产生一行
        int titleIndex = this.dataStartIndex - 1;
        row = sheet.createRow(titleIndex);
        /* *************创建列头名称*************** */
        for (int i = 0; i < this.fields.size(); i++) {
            ExcelItem field = this.fields.get(i);
            int col = i;
            // 根据指定的顺序获得列号
            if (field.getColumnIndex() < 0 && StringUtils.isNotBlank(field.getColumn())) {
                col = ExcelUtil.getExcelColIndex(field.getColumn());
                field.setColumnIndex(col);
            }
            // 创建列
            cell = row.createCell(col);
            // 对某个单元格加锁
            ExcelUtil.setDataLockValidation(sheet, cell);
            boolean isMarked = field.isMark()
                    || (null != markedIndexMap && markedIndexMap.contains(titleIndex + "_" + col));
            if (isMarked) {
                cell.setCellStyle(this.titleMarkCellStyle);
            } else {
                cell.setCellStyle(this.titleCellStyle);
            }

            // 设置列宽
            int columnWidth;
            if (null != this.columnWidthMap && null != this.columnWidthMap.get(col)) {
                columnWidth = this.columnWidthMap.get(col);
            } else {
                int titleBytes = (field.getName().getBytes().length <= 4 ? 6 : field.getName().getBytes().length);
                columnWidth = (int) (titleBytes * 1.5 * 256);
            }
            sheet.setColumnWidth(col, columnWidth);

            // 设置列中写入内容为 String 类型
            cell.setCellType(CellType.STRING);

            // 写入列名
            if (null != this.titleMap && null != this.titleMap.get(col)) {
                cell.setCellValue(titleMap.get(col));
            } else {
                cell.setCellValue(field.getName());
            }

            // 如果设置了提示信息则鼠标放上去提示.
            if (StringUtils.isNotBlank(field.getPrompt())) {
                // setPrompt(sheet, "", attr.prompt(), 1, 100, col, col);
                ExcelUtil.setComment(sheet, cell, "温馨提示", field.getPrompt(), this.xlsxFile);
            }

            if (null != this.lengthLimitMap && field.getByteLength() > 0) {
                this.lengthLimitMap.put(col, field.getByteLength());
            }

            // 如果设置了 combo 属性则本列只能选择不能输入
            if (field.getReadonlyCols().length > 0) {
                ExcelUtil.setValidation(sheet, field.getReadonlyCols(), 1, 100, col, col);
            }
        }
    }
}
