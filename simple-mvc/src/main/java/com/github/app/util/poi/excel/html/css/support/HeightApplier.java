package com.github.app.util.poi.excel.html.css.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import com.github.app.util.poi.excel.html.css.CssApplier;
import com.github.app.util.poi.excel.html.css.CssUtils;

/**
 * 行高相关
 *
 * @author a
 */
public class HeightApplier implements CssApplier {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> parse(Map<String, String> style) {
        Map<String, String> mapRtn = new HashMap<String, String>();
        String height = style.get(HEIGHT);
        if (CssUtils.isNum(height)) {
            mapRtn.put(HEIGHT, height);
        }
        return mapRtn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Cell cell, CellStyle cellStyle, Map<String, String> style) {
        int height = Math.round(CssUtils.getInt(style.get(HEIGHT)) * 255 / 12.75F);
        Row row = cell.getRow();
        if (height > row.getHeight()) {
            row.setHeight((short) height);
        }
    }
}
