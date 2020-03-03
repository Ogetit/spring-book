package com.github.app.util.poi.excel.html.css.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

import com.github.app.util.poi.excel.html.css.CssApplier;
import com.github.app.util.poi.excel.html.css.CssUtils;

/**
 * 宽度css
 *
 * @author a
 */
public class WidthApplier implements CssApplier {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> parse(Map<String, String> style) {
        Map<String, String> mapRtn = new HashMap<String, String>();
        String width = style.get(WIDTH);
        if (CssUtils.isNum(width)) {
            mapRtn.put(WIDTH, width);
        }
        return mapRtn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Cell cell, CellStyle cellStyle, Map<String, String> style) {
        int width;
        if (StringUtils.isNotBlank(style.get(WIDTH))) {
            width = Math.round(CssUtils.getInt(style.get(WIDTH)) * 2048 / 8.43F);
        } else {
            String value = cell.getStringCellValue();
            value = value == null ? " " : value;
            int valueBytes = (value.getBytes().length <= 4 ? 6 : value.getBytes().length);
            width = (int) (valueBytes * 1.5 * 256);
        }
        Sheet sheet = cell.getSheet();
        int colIndex = cell.getColumnIndex();
        if (width > sheet.getColumnWidth(colIndex)) {
            if (width > 255 * 256) {
                width = 255 * 256;
            }
            sheet.setColumnWidth(colIndex, width);
        }
    }
}
