package org.github.core.modules.jfchart;

import java.util.List;
import java.util.Map;

public class ChartParam {
    /**生成图表类型：pie-饼图；mutil-柱状折线混合图（目前只需要这两种）*/
    private String chartType;

    /**图表标题(可为空)*/
    private String chartTitle;

    /**X轴名称（Pie图不用）*/
    private String xName;

    /**Y轴名称（Pie图不用）*/
    private String yName;

    /**生成图片宽度（必传）*/
    private int width;

    /**生成图片高度（必传）*/
    private int height;

    /**生成Pie图所需数据*/
    private List<Map> pieList;

    /**生成图表图分析项中文名对应的key*/
    private String typeKey;

    /**生成Pie图数值对应的key*/
    private String pieValueKey;

    /**生成混合图柱状图数值对应的key（由于可能多列柱状图）*/
    private String[] barValueKey;

    /**生成混合图折线图数值对应的key（由于可能多个折线图）*/
    private String[] lineValueKey;

    /**生成混合图柱状图名称（由于可能多列柱状图，用作生成图例）*/
    private String[] barName;

    /**生成混合图折线图名称（由于可能多个折线图，用作生成图例）*/
    private String[] lineName;

    /**生成混合图所需柱状图Map*/
    private Map barMap;

    /**生成混合图所需折线图Map*/
    private Map lineMap;

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public String getxName() {
        return xName;
    }

    public void setxName(String xName) {
        this.xName = xName;
    }

    public String getyName() {
        return yName;
    }

    public void setyName(String yName) {
        this.yName = yName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Map> getPieList() {
        return pieList;
    }

    public void setPieList(List<Map> pieList) {
        this.pieList = pieList;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getPieValueKey() {
        return pieValueKey;
    }

    public void setPieValueKey(String pieValueKey) {
        this.pieValueKey = pieValueKey;
    }

    public String[] getBarValueKey() {
        return barValueKey;
    }

    public void setBarValueKey(String[] barValueKey) {
        this.barValueKey = barValueKey;
    }

    public String[] getLineValueKey() {
        return lineValueKey;
    }

    public void setLineValueKey(String[] lineValueKey) {
        this.lineValueKey = lineValueKey;
    }

    public String[] getBarName() {
        return barName;
    }

    public void setBarName(String[] barName) {
        this.barName = barName;
    }

    public String[] getLineName() {
        return lineName;
    }

    public void setLineName(String[] lineName) {
        this.lineName = lineName;
    }

    public Map getBarMap() {
        return barMap;
    }

    public void setBarMap(Map barMap) {
        this.barMap = barMap;
    }

    public Map getLineMap() {
        return lineMap;
    }

    public void setLineMap(Map lineMap) {
        this.lineMap = lineMap;
    }


}
