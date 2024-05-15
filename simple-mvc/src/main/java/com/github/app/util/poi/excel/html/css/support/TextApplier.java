package com.github.app.util.poi.excel.html.css.support;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.app.util.poi.excel.html.css.CssApplier;
import com.github.app.util.poi.excel.html.css.CssUtils;

/**
 * supports: 文本样式相关<br>
 * color: name | #rgb | #rrggbb | rgb(r, g, b) <br>
 * text-decoration: underline; <br>
 * font-style: italic | oblique; <br>
 * font-weight:  bold | bolder | 700 | 800 | 900; <br>
 * font-size: length; length unit will be ignored,
 * [xx-small|x-small|small|medium|large|x-large|xx-large] will be ignored. <br>
 * font：[[ font-style || font-variant || font-weight ]? font-size [/line-height]? font-family]
 * | caption | icon | menu | message-box | small-caption | status-bar;
 * [font-variant, line-height, caption, icon, menu, message-box, small-caption, status-bar] will be ignored.
 *
 * @author a
 */
public class TextApplier implements CssApplier {
    private static final Logger log =
            LoggerFactory.getLogger(TextApplier.class);

    private static final String TEXT_DECORATION = "text-decoration";
    private static final String UNDERLINE = "underline";

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> parse(Map<String, String> style) {
        log.debug("Parse Font Style.");
        Map<String, String> mapRtn = new HashMap<String, String>();
        // color
        String color = CssUtils.processColor(style.get(COLOR));
        if (StringUtils.isNotBlank(color)) {
            log.debug("Text Color [{}] Found.", color);
            mapRtn.put(COLOR, color);
        }
        // font
        parseFontAttr(style, mapRtn);
        // text text-decoration
        if (UNDERLINE.equals(style.get(TEXT_DECORATION))) {
            mapRtn.put(TEXT_DECORATION, UNDERLINE);
        }
        return mapRtn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Cell cell, CellStyle cellStyle, Map<String, String> style) {
        Workbook workBook = cell.getSheet().getWorkbook();
        Font font = null;
        if (ITALIC.equals(style.get(FONT_STYLE))) {
            font = getFont(cell, font);
            font.setItalic(true);
        }
        int fontSize = CssUtils.getInt(style.get(FONT_SIZE));
        if (fontSize > 0) {
            font = getFont(cell, font);
            font.setFontHeightInPoints((short) fontSize);
        }
        if (BOLD.equals(style.get(FONT_WEIGHT))) {
            font = getFont(cell, font);
            font.setBold(true);
        }
        String fontFamily = style.get(FONT_FAMILY);
        if (StringUtils.isNotBlank(fontFamily)) {
            font = getFont(cell, font);
            font.setFontName(fontFamily);
        }
        Color color0 = CssUtils.parseColor(workBook, style.get(COLOR));
        if (null != color0) {
            if (color0 instanceof HSSFColor) {
                HSSFColor color = (HSSFColor) color0;
                if (color != null) {
                    if (color.getIndex() != 0x8) {
                        font = getFont(cell, font);
                        font.setColor(color.getIndex());
                    } else {
                        log.info("Text Color [{}] Is Black Or Fimiliar To Black, Ignore.",
                                style.remove(COLOR));
                    }
                }
            } else {
                XSSFColor color = (XSSFColor) color0;
                if (color != null) {
                    if (color.getIndex() != 0x8) {
                        font = getFont(cell, font);
                        font.setColor(color.getIndex());
                    } else {
                        log.info("Text Color [{}] Is Black Or Fimiliar To Black, Ignore.",
                                style.remove(COLOR));
                    }
                }
            }
        }
        // text-decoration
        String textDecoration = style.get(TEXT_DECORATION);
        if (UNDERLINE.equals(textDecoration)) {
            font = getFont(cell, font);
            font.setUnderline(Font.U_SINGLE);
        }

        if (font != null) {
            cellStyle.setFont(font);
        }
    }

    // private methods
    private Map<String, String> parseFontAttr(Map<String, String> style, Map<String, String> mapRtn) {
        // font
        String font = style.get(FONT);
        if (StringUtils.isNotBlank(font) &&
                !ArrayUtils.contains(new String[] {
                        "small-caps", "caption",
                        "icon", "menu", "message-box",
                        "small-caption", "status-bar"
                }, font)) {
            log.debug("Parse Font Attr [{}].", font);
            String[] ignoreStyles = new String[] {
                    "normal",
                    // font weight normal
                    "[1-3]00"
            };
            StringBuffer sbFont = new StringBuffer(
                    font.replaceAll("^|\\s*" + StringUtils.join(ignoreStyles, "|") + "\\s+|$", " "));
            log.debug("Font Attr [{}] After Process Ingore.", sbFont);
            // style
            Matcher m = Pattern.compile("(?:^|\\s+)(italic|oblique)(?:\\s+|$)")
                    .matcher(sbFont.toString());
            if (m.find()) {
                sbFont.setLength(0);
                if (log.isDebugEnabled()) {
                    log.debug("Font Style [{}] Found.", m.group(1));
                }
                mapRtn.put(FONT_STYLE, ITALIC);
                m.appendReplacement(sbFont, " ");
                m.appendTail(sbFont);
            }
            // weight
            m = Pattern.compile("(?:^|\\s+)(bold(?:er)?|[7-9]00)(?:\\s+|$)")
                    .matcher(sbFont.toString());
            if (m.find()) {
                sbFont.setLength(0);
                if (log.isDebugEnabled()) {
                    log.debug("Font Weight [{}](bold) Found.", m.group(1));
                }
                mapRtn.put(FONT_WEIGHT, BOLD);
                m.appendReplacement(sbFont, " ");
                m.appendTail(sbFont);
            }
            // size xx-small | x-small | small | medium | large | x-large | xx-large | 18px [/2]
            m = Pattern.compile(
                    // before blank or start
                    new StringBuilder("(?:^|\\s+)")
                            // font size
                            .append("(xx-small|x-small|small|medium|large|x-large|xx-large|")
                            .append("(?:")
                            .append(PATTERN_LENGTH)
                            .append("))")
                            // line height
                            .append("(?:\\s*\\/\\s*(")
                            .append(PATTERN_LENGTH)
                            .append("))?")
                            // after blank or end
                            .append("(?:\\s+|$)")
                            .toString())
                    .matcher(sbFont.toString());
            if (m.find()) {
                sbFont.setLength(0);
                log.debug("Font Size[/line-height] [{}] Found.", m.group());
                String fontSize = m.group(1);
                if (StringUtils.isNotBlank(fontSize)) {
                    fontSize = StringUtils.deleteWhitespace(fontSize);
                    log.debug("Font Size [{}].", fontSize);
                    if (fontSize.matches(PATTERN_LENGTH)) {
                        mapRtn.put(FONT_SIZE, fontSize);
                    } else {
                        log.info("Font Size [{}] Not Supported, Ignore.", fontSize);
                    }
                }
                String lineHeight = m.group(2);
                if (StringUtils.isNotBlank(lineHeight)) {
                    log.info("Line Height [{}] Not Supported, Ignore.", lineHeight);
                }
                m.appendReplacement(sbFont, " ");
                m.appendTail(sbFont);
            }
            // font family
            if (sbFont.length() > 0) {
                log.debug("Font Families [{}].", sbFont);
                // trim & remove '"
                String fontFamily = sbFont.toString().split("\\s*,\\s*")[0].trim().replaceAll("'|\"", "");
                log.debug("Use First Font Family [{}].", fontFamily);
                mapRtn.put(FONT_FAMILY, fontFamily);
            }
        }
        font = style.get(FONT_STYLE);
        if (ArrayUtils.contains(new String[] {ITALIC, "oblique"}, font)) {
            log.debug("Font Italic [{}] Found.", font);
            mapRtn.put(FONT_STYLE, ITALIC);
        }
        font = style.get(FONT_WEIGHT);
        if (StringUtils.isNotBlank(font) &&
                Pattern.matches("^bold(?:er)?|[7-9]00$", font)) {
            log.debug("Font Weight [{}](bold) Found.", font);
            mapRtn.put(FONT_WEIGHT, BOLD);
        }
        font = style.get(FONT_SIZE);
        if (CssUtils.isNum(font)) {
            log.debug("Font Size [{}] Found.", font);
            mapRtn.put(FONT_SIZE, font);
        }
        font = style.get(FONT_FAMILY);
        if (StringUtils.isNotBlank(font)) {
            log.debug("Font Family [{}] Found.", font);
            mapRtn.put(FONT_FAMILY, font);
        }
        return mapRtn;
    }

    Font getFont(Cell cell, Font font) {
        if (font == null) {
            font = cell.getSheet().getWorkbook().createFont();
        }
        return font;
    }
}