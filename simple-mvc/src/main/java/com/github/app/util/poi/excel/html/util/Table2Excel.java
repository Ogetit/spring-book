package com.github.app.util.poi.excel.html.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.text.html.HTML;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.github.app.util.poi.excel.html.css.CssApplier;
import com.github.app.util.poi.excel.html.css.bean.XlsCssStyle;
import com.github.app.util.poi.excel.html.css.support.AlignApplier;
import com.github.app.util.poi.excel.html.css.support.BackgroundApplier;
import com.github.app.util.poi.excel.html.css.support.BorderApplier;
import com.github.app.util.poi.excel.html.css.support.HeightApplier;
import com.github.app.util.poi.excel.html.css.support.TextApplier;
import com.github.app.util.poi.excel.html.css.support.WidthApplier;

/**
 * html table 转 excel
 *
 * @author a
 */
public class Table2Excel {
    private static final Logger log =
            LoggerFactory.getLogger(Table2Excel.class);
    private static final List<CssApplier> STYLE_APPLIERS =
            new LinkedList<CssApplier>();

    // static init
    static {
        STYLE_APPLIERS.add(new AlignApplier());
        STYLE_APPLIERS.add(new BackgroundApplier());
        STYLE_APPLIERS.add(new WidthApplier());
        STYLE_APPLIERS.add(new HeightApplier());
        STYLE_APPLIERS.add(new BorderApplier());
        STYLE_APPLIERS.add(new TextApplier());
    }

    private Workbook workBook;
    private Sheet sheet;
    private Map<String, Object> cellsOccupied = new HashMap<String, Object>();
    private Map<String, CellStyle> cellStyles = new HashMap<String, CellStyle>();
    private CellStyle defaultCellStyle;
    private CellStyle titleCellStyle;
    private CellStyle numberCellStyle;
    private CellStyle numberPercentCellStyle;
    private int maxRow = 0;
    private int rowStartIndex = 0;
    private boolean thUseTitleStyle = false;

    // init
    private void init() {
        defaultCellStyle = workBook.createCellStyle();
        // defaultCellStyle.setWrapText(true);
        defaultCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("微软雅黑");
        defaultCellStyle.setFont(font);

        // border
        short black = Font.COLOR_NORMAL;
        BorderStyle thin = BorderStyle.THIN;
        // top
        defaultCellStyle.setBorderTop(thin);
        defaultCellStyle.setTopBorderColor(black);
        // right
        defaultCellStyle.setBorderRight(thin);
        defaultCellStyle.setRightBorderColor(black);
        // bottom
        defaultCellStyle.setBorderBottom(thin);
        defaultCellStyle.setBottomBorderColor(black);
        // left
        defaultCellStyle.setBorderLeft(thin);
        defaultCellStyle.setLeftBorderColor(black);

        titleCellStyle = workBook.createCellStyle();
        titleCellStyle.cloneStyleFrom(defaultCellStyle);
        Font titleFont = workBook.createFont();
        titleFont.setFontHeightInPoints((short) 10);
        titleFont.setFontName("微软雅黑");
        titleFont.setBold(true);
        titleCellStyle.setFont(titleFont);
        short color = 0x16;
        titleCellStyle.setFillForegroundColor(color);
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);

        numberCellStyle = workBook.createCellStyle();
        // top
        numberCellStyle.setBorderTop(thin);
        numberCellStyle.setTopBorderColor(black);
        // right
        numberCellStyle.setBorderRight(thin);
        numberCellStyle.setRightBorderColor(black);
        // bottom
        numberCellStyle.setBorderBottom(thin);
        numberCellStyle.setBottomBorderColor(black);
        // left
        numberCellStyle.setBorderLeft(thin);
        numberCellStyle.setLeftBorderColor(black);

        numberPercentCellStyle = workBook.createCellStyle();
        // top
        numberPercentCellStyle.setBorderTop(thin);
        numberPercentCellStyle.setTopBorderColor(black);
        // right
        numberPercentCellStyle.setBorderRight(thin);
        numberPercentCellStyle.setRightBorderColor(black);
        // bottom
        numberPercentCellStyle.setBorderBottom(thin);
        numberPercentCellStyle.setBottomBorderColor(black);
        // left
        numberPercentCellStyle.setBorderLeft(thin);
        numberPercentCellStyle.setLeftBorderColor(black);

        short dataFormatIndex = getDataFormatIndex(true, 2, false, null);
        numberCellStyle.setDataFormat(dataFormatIndex);
        numberCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        short dataPercentCellFormatIndex = getDataFormatIndex(true, 2, true, null);
        numberPercentCellStyle.setDataFormat(dataPercentCellFormatIndex);
        numberPercentCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    public Table2Excel(Sheet sheet) {
        this.sheet = sheet;
        this.workBook = sheet.getWorkbook();
        this.init();
    }

    /**
     * 将 html table 写入到 excel 内容
     *
     * @param html
     * @param defaultStyleList
     */
    public void html2sheet(String html, List<XlsCssStyle> defaultStyleList) {
        for (Element table : Jsoup.parseBodyFragment(html).select("table")) {
            table2Sheet(table, defaultStyleList);
        }
    }

    // private methods
    private void table2Sheet(Element table) {
        table2Sheet(table, null);
    }

    // private methods
    private void table2Sheet(Element table, List<XlsCssStyle> defaultStyleList) {
        int rowIndex = rowStartIndex;
        if (maxRow > rowStartIndex) {
            // blank row
            rowIndex = maxRow;
        }
        log.info("Interate Table Rows.");
        for (Element row : table.select("tr")) {
            log.info("Parse Table Row [{}]. Row Index [{}].", row, rowIndex);
            int colIndex = 0;
            log.info("Interate Cols.");
            for (Element td : row.select("td, th")) {
                // skip occupied cell
                while (cellsOccupied.get(rowIndex + "_" + colIndex) != null) {
                    log.info("Cell [{}][{}] Has Been Occupied, Skip.", rowIndex, colIndex);
                    ++colIndex;
                }
                log.info("Parse Col [{}], Col Index [{}].", td, colIndex);
                // 用户定义默认样式填充
                fillDefaultStyle(td, defaultStyleList, rowIndex, colIndex);

                int rowSpan = 0;
                String strRowSpan = td.attr("rowspan");
                if (StringUtils.isNotBlank(strRowSpan) &&
                        StringUtils.isNumeric(strRowSpan)) {
                    log.info("Found Row Span [{}].", strRowSpan);
                    rowSpan = Integer.parseInt(strRowSpan);
                }
                int colSpan = 0;
                String strColSpan = td.attr("colspan");
                if (StringUtils.isNotBlank(strColSpan) &&
                        StringUtils.isNumeric(strColSpan)) {
                    log.info("Found Col Span [{}].", strColSpan);
                    colSpan = Integer.parseInt(strColSpan);
                }
                // col span & row span
                if (colSpan > 1 && rowSpan > 1) {
                    spanRowAndCol(td, rowIndex, colIndex, rowSpan, colSpan);
                    colIndex += colSpan;
                }
                // col span only
                else if (colSpan > 1) {
                    spanCol(td, rowIndex, colIndex, colSpan);
                    colIndex += colSpan;
                }
                // row span only
                else if (rowSpan > 1) {
                    spanRow(td, rowIndex, colIndex, rowSpan);
                    ++colIndex;
                }
                // no span
                else {
                    getOrCreateCell(td, getOrCreateRow(rowIndex), colIndex);
                    ++colIndex;
                }
            }
            ++rowIndex;
        }
        maxRow = rowIndex;
    }

    // 默认样式填充
    private void fillDefaultStyle(Element td, List<XlsCssStyle> defaultStyleList, int rowIndex, int colIndex) {
        if (CollectionUtils.isEmpty(defaultStyleList)) {
            return;
        }
        for (XlsCssStyle style : defaultStyleList) {
            if (StringUtils.isBlank(style.getStyle())) {
                continue;
            }
            boolean forOneCell = Objects.equals(style.getCol(), colIndex)
                    && Objects.equals(style.getRow(), rowIndex);
            boolean forAll = null == style.getCol() && null == style.getRow();
            boolean forRow = Objects.equals(style.getRow(), rowIndex) && null == style.getCol();
            boolean forCol = Objects.equals(style.getCol(), colIndex) && null == style.getRow();
            if (forOneCell || forRow || forCol || forAll) {
                String beforeStyle = td.attr(CssApplier.STYLE);
                if (StringUtils.isBlank(beforeStyle)) {
                    td.attr(CssApplier.STYLE, style.getStyle());
                } else {
                    if (style.isOverEffect()) {
                        String split = beforeStyle.endsWith(";") ? "" : ";";
                        td.attr(CssApplier.STYLE, beforeStyle + split + style.getStyle());
                    } else {
                        String split = style.getStyle().endsWith(";") ? "" : ";";
                        td.attr(CssApplier.STYLE, style.getStyle() + split + beforeStyle);
                    }
                }
            }
        }
    }

    private void spanRow(Element td, int rowIndex, int colIndex, int rowSpan) {
        log.info("Span Row , From Row [{}], Span [{}].", rowIndex, rowSpan);
        mergeRegion(rowIndex, rowIndex + rowSpan - 1, colIndex, colIndex);
        for (int i = 0; i < rowSpan; ++i) {
            Row row = getOrCreateRow(rowIndex + i);
            getOrCreateCell(td, row, colIndex);
            cellsOccupied.put((rowIndex + i) + "_" + colIndex, true);
        }

    }

    private void spanCol(Element td, int rowIndex, int colIndex, int colSpan) {
        log.info("Span Col, From Col [{}], Span [{}].", colIndex, colSpan);
        mergeRegion(rowIndex, rowIndex, colIndex, colIndex + colSpan - 1);
        Row row = getOrCreateRow(rowIndex);
        for (int i = 0; i < colSpan; ++i) {
            getOrCreateCell(td, row, colIndex + i);
        }
    }

    private void spanRowAndCol(Element td, int rowIndex, int colIndex,
                               int rowSpan, int colSpan) {
        log.info("Span Row And Col, From Row [{}], Span [{}].", rowIndex, rowSpan);
        log.info("From Col [{}], Span [{}].", colIndex, colSpan);
        mergeRegion(rowIndex, rowIndex + rowSpan - 1, colIndex, colIndex + colSpan - 1);
        for (int i = 0; i < rowSpan; ++i) {
            Row row = getOrCreateRow(rowIndex + i);
            for (int j = 0; j < colSpan; ++j) {
                getOrCreateCell(td, row, colIndex + j);
                cellsOccupied.put((rowIndex + i) + "_" + (colIndex + j), true);
            }
        }
        getOrCreateCell(td, getOrCreateRow(rowIndex), colIndex);
    }

    private void fillGoodText(Cell cell, Element td) {
        String text = getGoodText(td);
        String tempText = text.replaceAll(",", "").replaceAll("%", "");
        if (NumberUtils.isNumber(tempText)) {
            if (text.indexOf("%") >= 0) {
                cell.setCellValue(NumberUtils.toDouble(tempText, 0) / 100);
                cell.setCellType(CellType.NUMERIC);
                cell.setCellStyle(numberPercentCellStyle);
            } else {
                cell.setCellValue(NumberUtils.toDouble(tempText, 0));
                cell.setCellType(CellType.NUMERIC);
                cell.setCellStyle(numberCellStyle);
            }
        } else {
            cell.setCellValue(text);
        }
    }

    private short getDataFormatIndex(boolean thousands, Integer pointIndex, boolean percent, Integer negativeStyle) {
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
            switch (pointIndex) {
                case -1:
                case 2:
                    positiveNum.append("0.00");
                    negativeNum.append("0.00");
                    break;
                case 1:
                    positiveNum.append("0.0");
                    negativeNum.append("0.0");
                    break;
                case 3:
                    positiveNum.append("0.000");
                    negativeNum.append("0.000");
                    break;
                case 4:
                    positiveNum.append("0.0000");
                    negativeNum.append("0.0000");
                    break;
                case 5:
                    positiveNum.append("0.00000");
                    negativeNum.append("0.00000");
                    break;
                case 6:
                    positiveNum.append("0.000000");
                    negativeNum.append("0.000000");
                    break;
                default:
                    positiveNum.append("0");
                    negativeNum.append("0");
                    break;
            }
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
        DataFormat dataFormatNew = workBook.createDataFormat();
        short formatIndex = dataFormatNew.getFormat(positiveNum + "_);" + negativeNum);
        return formatIndex;
    }

    // 获取优质text
    private String getGoodText(Element td) {
        String html = td.html();
        if (StringUtils.isBlank(html)) {
            return "";
        }
        html = html.replaceAll("</div>", "#LINE-END#</div>");
        html = html.replaceAll("<br[^>]*>", "#LINE-END#</div>");
        html = html.replaceAll("</p>", "\r\n</p>");
        String text = Jsoup.parseBodyFragment(html).text();
        int length = "#LINE-END#".length();
        text = text.trim();
        while (text.startsWith("#LINE-END#")) {
            text = text.substring(length);
            text = text.trim();
        }
        while (text.endsWith("#LINE-END#")) {
            text = text.substring(0, text.length() - length);
            text = text.trim();
        }
        text = text.replace("#LINE-END#", "\r\n");
        return text;
    }

    private Cell getOrCreateCell(Element td, Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            log.debug("Create Cell [{}][{}].", row.getRowNum(), colIndex);
            cell = row.createCell(colIndex);
        }
        fillGoodText(cell, td);
        // 填充文本样式
        return applyStyle(td, cell);
    }

    private Cell applyStyle(Element td, Cell cell) {
        String style = td.attr(CssApplier.STYLE);
        CellStyle cellStyle = null;
        if (thUseTitleStyle && td.tagName().equals(HTML.Tag.TH.toString())) {
            // 设置置标题样式
            cellStyle = titleCellStyle;
        } else if (StringUtils.isNotBlank(style)) {
            if (cellStyles.size() < 4000) {
                Map<String, String> mapStyle = parseStyle(style.trim());
                Map<String, String> mapStyleParsed = new HashMap<String, String>();
                for (CssApplier applier : STYLE_APPLIERS) {
                    mapStyleParsed.putAll(applier.parse(mapStyle));
                }
                cellStyle = cellStyles.get(styleStr(mapStyleParsed));
                if (cellStyle == null) {
                    log.debug("No Cell Style Found In Cache, Parse New Style.");
                    cellStyle = workBook.createCellStyle();
                    cellStyle.cloneStyleFrom(defaultCellStyle);
                    for (CssApplier applier : STYLE_APPLIERS) {
                        applier.apply(cell, cellStyle, mapStyleParsed);
                    }
                    // cache style
                    cellStyles.put(styleStr(mapStyleParsed), cellStyle);
                }
            } else {
                log.info("Custom Cell Style Exceeds 4000, Could Not Create New Style, Use Default Style.");
                cellStyle = defaultCellStyle;
            }
        } else {
            log.debug("Use Default Cell Style.");
            cellStyle = defaultCellStyle;
        }
        if (numberCellStyle.equals(cell.getCellStyle())) {
            CellStyle style1 = workBook.createCellStyle();
            style1.cloneStyleFrom(cellStyle);
            style1.setDataFormat(numberCellStyle.getDataFormat());
            cell.setCellStyle(style1);
        } else if (numberPercentCellStyle.equals(cell.getCellStyle())) {
            CellStyle style1 = workBook.createCellStyle();
            style1.cloneStyleFrom(cellStyle);
            style1.setDataFormat(numberPercentCellStyle.getDataFormat());
            cell.setCellStyle(style1);
        } else {
            cell.setCellStyle(cellStyle);
        }
        return cell;
    }

    private String styleStr(Map<String, String> style) {
        log.debug("Build Style String, Style [{}].", style);
        StringBuilder sbStyle = new StringBuilder();
        Object[] keys = style.keySet().toArray();
        Arrays.sort(keys);
        for (Object key : keys) {
            sbStyle.append(key)
                    .append(':')
                    .append(style.get(key))
                    .append(';');
        }
        log.debug("Style String Result [{}].", sbStyle);
        return sbStyle.toString();
    }

    private Map<String, String> parseStyle(String style) {
        log.debug("Parse Style String [{}] To Map.", style);
        Map<String, String> mapStyle = new HashMap<String, String>();
        for (String s : style.split("\\s*;\\s*")) {
            if (StringUtils.isNotBlank(s)) {
                String[] ss = s.split("\\s*\\:\\s*");
                if (ss.length == 2 &&
                        StringUtils.isNotBlank(ss[0]) &&
                        StringUtils.isNotBlank(ss[1])) {
                    String attrName = ss[0].toLowerCase();
                    String attrValue = ss[1];
                    // do not change font name
                    if (!CssApplier.FONT.equals(attrName) &&
                            !CssApplier.FONT_FAMILY.equals(attrName)) {
                        attrValue = attrValue.toLowerCase();
                    }
                    mapStyle.put(attrName, attrValue);
                }
            }
        }
        log.debug("Style Map Result [{}].", mapStyle);
        return mapStyle;
    }

    private Row getOrCreateRow(int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            log.info("Create New Row [{}].", rowIndex);
            row = sheet.createRow(rowIndex);
            if (rowIndex > maxRow) {
                maxRow = rowIndex;
            }
        }
        return row;
    }

    private void mergeRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
        log.debug("Merge Region, From Row [{}], To [{}].", firstRow, lastRow);
        log.debug("From Col [{}], To [{}].", firstCol, lastCol);
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    public int getRowStartIndex() {
        return rowStartIndex;
    }

    public void setRowStartIndex(int rowStartIndex) {
        this.rowStartIndex = rowStartIndex;
        this.maxRow = rowStartIndex;
    }

    public CellStyle getNumberCellStyle() {
        return numberCellStyle;
    }

    public void setThUseTitleStyle(boolean thUseTitleStyle) {
        this.thUseTitleStyle = thUseTitleStyle;
    }
}
