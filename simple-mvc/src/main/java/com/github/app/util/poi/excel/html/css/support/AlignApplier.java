package com.github.app.util.poi.excel.html.css.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import com.github.app.util.poi.excel.html.css.CssApplier;

/**
 * 对齐相关
 *
 * @author a
 */
public class AlignApplier implements CssApplier {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> parse(Map<String, String> style) {
        Map<String, String> mapRtn = new HashMap<String, String>();
        String align = style.get(TEXT_ALIGN);
        if (!ArrayUtils.contains(new String[] {LEFT, CENTER, RIGHT, JUSTIFY}, align)) {
            align = LEFT;
        }
        mapRtn.put(TEXT_ALIGN, align);
        align = style.get(VETICAL_ALIGN);
        if (!ArrayUtils.contains(new String[] {TOP, MIDDLE, BOTTOM}, align)) {
            align = MIDDLE;
        }
        mapRtn.put(VETICAL_ALIGN, align);
        return mapRtn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Cell cell, CellStyle cellStyle,
                      Map<String, String> style) {
        // text align
        String align = style.get(TEXT_ALIGN);
        HorizontalAlignment sAlign = HorizontalAlignment.LEFT;
        if (RIGHT.equals(align)) {
            sAlign = HorizontalAlignment.RIGHT;
        } else if (CENTER.equals(align)) {
            sAlign = HorizontalAlignment.CENTER;
        } else if (JUSTIFY.equals(align)) {
            sAlign = HorizontalAlignment.JUSTIFY;
        }
        cellStyle.setAlignment(sAlign);
        // vertical align
        align = style.get(VETICAL_ALIGN);
        VerticalAlignment sAlign2 = VerticalAlignment.CENTER;
        if (TOP.equals(align)) {
            sAlign2 = VerticalAlignment.TOP;
        } else if (BOTTOM.equals(align)) {
            sAlign2 = VerticalAlignment.BOTTOM;
        } else if (JUSTIFY.equals(align)) {
            sAlign2 = VerticalAlignment.JUSTIFY;
        }
        cellStyle.setVerticalAlignment(sAlign2);
    }
}
