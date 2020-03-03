package com.github.app.util.poi.excel.html.css.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.app.util.poi.excel.html.css.CssApplier;
import com.github.app.util.poi.excel.html.css.CssUtils;

/**
 * 边框相关
 * border[-[pos][-attr]]: [border-width] || [border-style] || [border-color]; <br>
 * border-style: none | hidden | dotted | dashed | solid | double
 *
 * @author a
 */
public class BorderApplier implements CssApplier {
    private static final Logger log = LoggerFactory.getLogger(BorderApplier.class);
    private static final String NONE = "none";
    private static final String HIDDEN = "hidden";
    private static final String SOLID = "solid";
    private static final String DOUBLE = "double";
    private static final String DOTTED = "dotted";
    private static final String DASHED = "dashed";
    // border styles
    private static final String[] BORDER_STYLES = new String[] {
            // Specifies no border
            NONE,
            // The same as "none", except in border conflict resolution for table elements
            HIDDEN,
            // Specifies a dotted border
            DOTTED,
            // Specifies a dashed border
            DASHED,
            // Specifies a solid border
            SOLID,
            // Specifies a double border
            DOUBLE
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> parse(Map<String, String> style) {
        Map<String, String> mapRtn = new HashMap<String, String>();
        for (String pos : new String[] {null, TOP, RIGHT, BOTTOM, LEFT}) {
            // border[-attr]
            if (pos == null) {
                setBorderAttr(mapRtn, pos, style.get(BORDER));
                setBorderAttr(mapRtn, pos, style.get(BORDER + "-" + COLOR));
                setBorderAttr(mapRtn, pos, style.get(BORDER + "-" + WIDTH));
                setBorderAttr(mapRtn, pos, style.get(BORDER + "-" + STYLE));
            }
            // border-pos[-attr]
            else {
                setBorderAttr(mapRtn, pos, style.get(BORDER + "-" + pos));
                for (String attr : new String[] {COLOR, WIDTH, STYLE}) {
                    String attrName = BORDER + "-" + pos + "-" + attr;
                    String attrValue = style.get(attrName);
                    if (StringUtils.isNotBlank(attrValue)) {
                        mapRtn.put(attrName, attrValue);
                    }
                }
            }
        }
        return mapRtn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Cell cell, CellStyle cellStyle, Map<String, String> style) {
        for (String pos : new String[] {TOP, RIGHT, BOTTOM, LEFT}) {
            String posName = StringUtils.capitalize(pos.toLowerCase());
            // color
            String colorAttr = BORDER + "-" + pos + "-" + COLOR;
            Color poiColor0 = CssUtils.parseColor(cell.getSheet().getWorkbook(), style.get(colorAttr));
            if (poiColor0 != null) {
                try {
                    if (poiColor0 instanceof HSSFColor) {
                        HSSFColor poiColor = (HSSFColor) poiColor0;
                        MethodUtils.invokeMethod(cellStyle, "set" + posName + "BorderColor", poiColor.getIndex());
                    } else {
                        XSSFColor poiColor = (XSSFColor) poiColor0;
                        MethodUtils.invokeMethod(cellStyle, "set" + posName + "BorderColor", poiColor.getIndex());
                    }
                } catch (Exception e) {
                    log.error("Set Border Color Error Caused.", e);
                }
            }
            // width
            int width = CssUtils.getInt(style.get(BORDER + "-" + pos + "-" + WIDTH));
            String styleAttr = BORDER + "-" + pos + "-" + STYLE;
            String styleValue = style.get(styleAttr);
            BorderStyle borderStyle = BorderStyle.NONE;
            // empty or solid
            if (StringUtils.isBlank(styleValue) || "solid".equals(styleValue)) {
                if (width > 2) {
                    borderStyle = BorderStyle.THICK;
                } else if (width > 1) {
                    borderStyle = BorderStyle.MEDIUM;
                } else {
                    borderStyle = BorderStyle.THIN;
                }
            } else if (ArrayUtils.contains(new String[] {NONE, HIDDEN}, styleValue)) {
                borderStyle = BorderStyle.NONE;
            } else if (DOUBLE.equals(styleValue)) {
                borderStyle = BorderStyle.DOUBLE;
            } else if (DOTTED.equals(styleValue)) {
                borderStyle = BorderStyle.DOTTED;
            } else if (DASHED.equals(styleValue)) {
                if (width > 1) {
                    borderStyle = BorderStyle.MEDIUM_DASHED;
                } else {
                    borderStyle = BorderStyle.DASHED;
                }
            }
            // border style
            if (borderStyle != BorderStyle.NONE) {
                try {
                    MethodUtils.invokeMethod(cellStyle, "setBorder" + posName, borderStyle);
                } catch (Exception e) {
                    log.error("Set Border Style Error Caused.", e);
                }
            }
        }
    }

    // --
    // private methods

    private void setBorderAttr(Map<String, String> mapBorder, String pos, String value) {
        if (StringUtils.isNotBlank(value)) {
            String borderColor = null;
            for (String borderAttr : value.split("\\s+")) {
                if ((borderColor = CssUtils.processColor(borderAttr)) != null) {
                    setBorderAttr(mapBorder, pos, COLOR, borderColor);
                } else if (CssUtils.isNum(borderAttr)) {
                    setBorderAttr(mapBorder, pos, WIDTH, borderAttr);
                } else if (isStyle(borderAttr)) {
                    setBorderAttr(mapBorder, pos, STYLE, borderAttr);
                } else {
                    log.info("Border Attr [{}] Is Not Suppoted.", borderAttr);
                }
            }
        }
    }

    private void setBorderAttr(Map<String, String> mapBorder, String pos,
                               String attr, String value) {
        if (StringUtils.isNotBlank(pos)) {
            mapBorder.put(BORDER + "-" + pos + "-" + attr, value);
        } else {
            for (String name : new String[] {TOP, RIGHT, BOTTOM, LEFT}) {
                mapBorder.put(BORDER + "-" + name + "-" + attr, value);
            }
        }
    }

    private boolean isStyle(String value) {
        return ArrayUtils.contains(BORDER_STYLES, value);
    }
}
