package com.github.app.util.poi.excel.html.css.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFColor;

import com.github.app.util.poi.excel.html.css.CssApplier;
import com.github.app.util.poi.excel.html.css.CssUtils;

/**
 * 背景相关
 *
 * @author a
 */
public class BackgroundApplier implements CssApplier {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> parse(Map<String, String> style) {
        Map<String, String> mapRtn = new HashMap<String, String>();
        String bg = style.get(BACKGROUND);
        String bgColor = null;
        if (StringUtils.isNotBlank(bg)) {
            for (String bgAttr : bg.split("(?<=\\)|\\w|%)\\s+(?=\\w)")) {
                if ((bgColor = CssUtils.processColor(bgAttr)) != null) {
                    mapRtn.put(BACKGROUND_COLOR, bgColor);
                    break;
                }
            }
        }
        bg = style.get(BACKGROUND_COLOR);
        if (StringUtils.isNotBlank(bg) &&
                (bgColor = CssUtils.processColor(bg)) != null) {
            mapRtn.put(BACKGROUND_COLOR, bgColor);

        }
        if (bgColor != null) {
            bgColor = mapRtn.get(BACKGROUND_COLOR);
            if ("#ffffff".equals(bgColor)) {
                mapRtn.remove(BACKGROUND_COLOR);
            }
        }
        return mapRtn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Cell cell, CellStyle cellStyle, Map<String, String> style) {
        String bgColor = style.get(BACKGROUND_COLOR);
        if (StringUtils.isNotBlank(bgColor)) {
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            short colorIndex = 0x16;
            if (!"grey".equals(bgColor)) {
                Color color = CssUtils.parseColor(cell.getSheet().getWorkbook(), bgColor);
                if (null == color) {
                    return;
                }
                if (color instanceof HSSFColor) {
                    colorIndex = ((HSSFColor) color).getIndex();
                } else {
                    colorIndex = ((XSSFColor) color).getIndex();
                }
            }
            cellStyle.setFillForegroundColor(colorIndex);
        }
    }
}
