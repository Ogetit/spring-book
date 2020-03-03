package com.github.app.util.poi.excel.html.css;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Css工具类
 *
 * @author a
 */
public class CssUtils {
    private static final Logger log = LoggerFactory.getLogger(CssUtils.class);
    // matches #rgb
    private static final String COLOR_PATTERN_VALUE_SHORT = "^(#(?:[a-f]|\\d){3})$";
    // matches #rrggbb
    private static final String COLOR_PATTERN_VALUE_LONG = "^(#(?:[a-f]|\\d{2}){3})$";
    // matches #rgb(r, g, b)
    private static final String COLOR_PATTERN_RGB = "^(rgb\\s*\\(\\s*(.+)\\s*,\\s*(.+)\\s*,\\s*(.+)\\s*\\))$";
    // color name -> POI Color
    private static Map<String, Color> colors = new HashMap<String, Color>();

    /**
     * get color name
     *
     * @param color HSSFColor
     *
     * @return color name
     */
    private static String colorName(Class<? extends Color> color) {
        return color.getSimpleName().replace("_", "").toLowerCase();
    }

    /**
     * get int value of string
     *
     * @param strValue string value
     *
     * @return int value
     */
    public static int getInt(String strValue) {
        int value = 0;
        if (StringUtils.isNotBlank(strValue)) {
            Matcher m = Pattern.compile("^(\\d+)(?:\\w+|%)?$").matcher(strValue);
            if (m.find()) {
                value = Integer.parseInt(m.group(1));
            }
        }
        return value;
    }

    /**
     * check number string
     *
     * @param strValue string
     *
     * @return true if string is number
     */
    public static boolean isNum(String strValue) {
        return StringUtils.isNotBlank(strValue) && strValue.matches("^\\d+(\\w+|%)?$");
    }

    /**
     * process color
     *
     * @param color color to process
     *
     * @return color after process
     */
    public static String processColor(String color) {
        log.info("Process Color [{}].", color);
        String colorRtn = null;
        if (StringUtils.isNotBlank(color)) {
            // #rgb -> #rrggbb
            if (color.matches(COLOR_PATTERN_VALUE_SHORT)) {
                log.debug("Short Hex Color [{}] Found.", color);
                StringBuffer sbColor = new StringBuffer();
                Matcher m = Pattern.compile("([a-f]|\\d)").matcher(color);
                while (m.find()) {
                    m.appendReplacement(sbColor, "$1$1");
                }
                colorRtn = sbColor.toString();
                log.debug("Translate Short Hex Color [{}] To [{}].", color, colorRtn);
            }
            // #rrggbb
            else if (color.matches(COLOR_PATTERN_VALUE_LONG)) {
                colorRtn = color;
                log.debug("Hex Color [{}] Found, Return.", color);
            }
            // rgb(r, g, b)
            else if (color.matches(COLOR_PATTERN_RGB)) {
                Matcher m = Pattern.compile(COLOR_PATTERN_RGB).matcher(color);
                if (m.matches()) {
                    log.debug("RGB Color [{}] Found.", color);
                    colorRtn = convertColor(calcColorValue(m.group(2)),
                            calcColorValue(m.group(3)),
                            calcColorValue(m.group(4)));
                    log.debug("Translate RGB Color [{}] To Hex [{}].", color, colorRtn);
                }
            }
            // color name, red, green, ...
            else {
                Color poiColor = getColor(color);
                if (poiColor != null && poiColor instanceof HSSFColor) {
                    log.debug("Color Name [{}] Found.", color);
                    short[] t = ((HSSFColor) poiColor).getTriplet();
                    colorRtn = convertColor(t[0], t[1], t[2]);
                    log.debug("Translate Color Name [{}] To Hex [{}].", color, colorRtn);
                } else {
                    colorRtn = color;
                }
            }
        }
        return colorRtn;
    }

    /**
     * parse color
     *
     * @param workBook0 work book
     * @param color     string color
     *
     * @return HSSFColor
     */
    public static Color parseColor(Workbook workBook0, String color) {
        if (StringUtils.isBlank(color)) {
            return null;
        }
        Color poiColor = null;
        java.awt.Color awtColor = java.awt.Color.decode(color);
        if (awtColor == null) {
            return null;
        }
        if (workBook0 instanceof HSSFWorkbook) {
            HSSFWorkbook workbook = (HSSFWorkbook) workBook0;

            int r = awtColor.getRed();
            int g = awtColor.getGreen();
            int b = awtColor.getBlue();
            HSSFPalette palette = workbook.getCustomPalette();
            poiColor = palette.findColor((byte) r, (byte) g, (byte) b);
            if (poiColor == null) {
                poiColor = palette.findSimilarColor(r, g, b);
            }
        } else {
            poiColor = new XSSFColor(awtColor);
        }
        return poiColor;
    }

    // --
    // private methods

    private static Color getColor(String color) {
        return colors.get(color.replace("_", ""));
    }

    private static String convertColor(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    private static int calcColorValue(String color) {
        int rtn = 0;
        // matches 64 or 64%
        Matcher m = Pattern.compile("^(\\d*\\.?\\d+)\\s*(%)?$").matcher(color);
        if (m.matches()) {
            // % not found
            if (m.group(2) == null) {
                rtn = Math.round(Float.parseFloat(m.group(1))) % 256;
            } else {
                rtn = Math.round(Float.parseFloat(m.group(1)) * 255 / 100) % 256;
            }
        }
        return rtn;
    }
}
