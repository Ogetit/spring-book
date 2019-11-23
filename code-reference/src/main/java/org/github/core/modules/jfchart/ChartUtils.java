package org.github.core.modules.jfchart;

import org.github.core.modules.utils.StrUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;
import org.github.core.modules.utils.DateUtil;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChartUtils {

    private static Font FONT = new Font("宋体", Font.PLAIN, 12);
    public static Color[] CHART_COLORS = {
            new Color(99, 148, 188), new Color(144, 237, 125), new Color(255, 188, 117),
            new Color(153, 158, 255), new Color(255, 117, 153), new Color(253, 236, 109), new Color(128, 133, 232),
            new Color(158, 90, 102), new Color(255, 204, 102)};// 颜色

    /**
     * 中文主题样式 解决乱码
     */
    public static void setChartTheme() {
        // 设置中文主题样式 解决乱码
        StandardChartTheme chartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        chartTheme.setExtraLargeFont(FONT);
        // 设置图例的字体
        chartTheme.setRegularFont(FONT);
        // 设置轴向的字体
        chartTheme.setLargeFont(FONT);
        chartTheme.setSmallFont(FONT);
        chartTheme.setTitlePaint(new Color(51, 51, 51));
        chartTheme.setSubtitlePaint(new Color(85, 85, 85));

        chartTheme.setLegendBackgroundPaint(Color.WHITE);// 设置标注
        chartTheme.setLegendItemPaint(Color.BLACK);//
        chartTheme.setChartBackgroundPaint(Color.WHITE);
        // 绘制颜色绘制颜色.轮廓供应商
        // paintSequence,outlinePaintSequence,strokeSequence,outlineStrokeSequence,shapeSequence

        Paint[] OUTLINE_PAINT_SEQUENCE = new Paint[]{Color.WHITE};
        // 绘制器颜色源
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(CHART_COLORS, CHART_COLORS, OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        chartTheme.setDrawingSupplier(drawingSupplier);

        chartTheme.setPlotBackgroundPaint(Color.WHITE);// 绘制区域
        chartTheme.setPlotOutlinePaint(Color.WHITE);// 绘制区域外边框
        chartTheme.setLabelLinkPaint(new Color(8, 55, 114));// 链接标签颜色
        chartTheme.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);

        chartTheme.setAxisOffset(new RectangleInsets(5, 12, 5, 12));
        chartTheme.setDomainGridlinePaint(new Color(192, 208, 224));// X坐标轴垂直网格颜色
        chartTheme.setRangeGridlinePaint(new Color(192, 192, 192));// Y坐标轴水平网格颜色

        chartTheme.setBaselinePaint(Color.WHITE);
        chartTheme.setCrosshairPaint(Color.BLUE);// 不确定含义
        chartTheme.setAxisLabelPaint(new Color(51, 51, 51));// 坐标轴标题文字颜色
        chartTheme.setTickLabelPaint(new Color(67, 67, 72));// 刻度数字
        chartTheme.setBarPainter(new StandardBarPainter());// 设置柱状图渲染
        chartTheme.setXYBarPainter(new StandardXYBarPainter());// XYBar 渲染

        chartTheme.setItemLabelPaint(Color.black);
        chartTheme.setThermometerPaint(Color.white);// 温度计

        ChartFactory.setChartTheme(chartTheme);
    }

    public static void createChartPng(OutputStream out, ChartParam chart) throws IOException {
        setChartTheme();
        String chartType = chart.getChartType();
        JFreeChart jfreeChart = null;
        if ("pie".equals(chartType)) {//生成饼状图
            jfreeChart = createDefaultPieChart(chart.getPieList(), chart.getTypeKey(), chart.getPieValueKey(), chart.getChartTitle());
        } else if ("mutil".equals(chartType)) {
            //获取柱状图所需数据
            DefaultCategoryDataset barDate = createBarChart(chart.getBarMap(), chart.getTypeKey(), chart.getBarValueKey(), chart.getBarName());
            //创建柱状图
            jfreeChart = createBarChart(barDate, chart.getChartTitle(), "", chart.getyName());
            //组合折线图所需数据
            DefaultCategoryDataset lineDate = createBarChart(chart.getLineMap(), chart.getTypeKey(), chart.getLineValueKey(), chart.getLineName());
            //在已创建好的柱状图上加上折线图
            addLineChartToBar(jfreeChart, lineDate);
        }
        if (null != jfreeChart) {
            // 保存为PNG
            ChartUtilities.writeChartAsPNG(out, jfreeChart, chart.getWidth(), chart.getHeight());
            out.flush();
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    /**
     * 生成饼图
     *
     * @param list
     * @param typeKey  中文名对应的key
     * @param valueKey 数值对应的key
     * @param title    饼图标题
     * @return
     */
    public static JFreeChart createDefaultPieChart(List<Map> list, String typeKey, String valueKey, String title) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map one : list) {
            String type = StrUtil.getValue(one, typeKey);
            String value = StrUtil.getValue(one, valueKey);
            dataset.setValue(type, Double.valueOf(value));
        }
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, false, false);
        //饼图样式渲染
        setPieRender(chart, title);
        return chart;
    }

    /**
     * 设置饼状图渲染
     *
     * @param chart 饼图对象
     * @param title 饼图标题
     */
    public static void setPieRender(JFreeChart chart, String title) {
        //设置图表标题
        chart.setTitle(new TextTitle(title, new Font("宋体", Font.BOLD, 15)));
        //设置Legend字体
        chart.getLegend().setItemFont(new Font("宋体", Font.ROMAN_BASELINE, 12));
        chart.setBorderVisible(false);//设置边框不可见
        Plot plot = chart.getPlot();
        plot.setNoDataMessage("数据加载失败");
        plot.setNoDataMessageFont(new Font("宋体", Font.BOLD, 12));
        PiePlot piePlot = (PiePlot) plot;
        plot.setOutlinePaint(Color.WHITE); // 设置绘图面板外边的填充颜色
        ((PiePlot) plot).setLabelLinkStyle(PieLabelLinkStyle.STANDARD);//将饼图链接标签的线设置为直线，可选参数CUBIC_CURVE, QUAD_CURVE, STANDARD
        piePlot.setLabelFont(new Font("宋体", Font.ROMAN_BASELINE, 12));    //设置图例字体（不设置会中文乱码）
        piePlot.setIgnoreNullValues(true);
        piePlot.setLabelBackgroundPaint(Color.WHITE);// 去掉背景色
        piePlot.setLabelShadowPaint(Color.WHITE);// 去掉阴影
        piePlot.setLabelOutlinePaint(Color.WHITE);// 去掉边框
        piePlot.setShadowPaint(Color.WHITE);
        piePlot.setBackgroundPaint(Color.WHITE);
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        // 设置扇区标签显示格式：自定义方式，{0} 表示选项， {1} 表示数值， {2} 表示所占比,小数点后一位
        piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{2}", NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
        // 指定图片的透明度(0.0-1.0)
        piePlot.setBackgroundImageAlpha(1.0f);
        piePlot.setSectionPaint(0, Color.decode("#1BB2D8"));
        piePlot.setSectionPaint(1, Color.decode("#1790CF"));
        piePlot.setSectionPaint(2, Color.decode("#AFD6DD"));
        piePlot.setSectionPaint(3, Color.decode("#88B0BB"));
        piePlot.setSectionPaint(4, Color.decode("#1C7099"));
        piePlot.setSectionPaint(5, Color.decode("#038CC4"));
        piePlot.setSectionPaint(6, Color.decode("#75ABD0"));
    }


//	创建柱状折线混合图
//		前期数据转化，需要将list<Map> 转化为Map
//		数据格式：
//			map（typeKey,new String[]{"一月","二月","三月","四月","五月","六月"}）
//			map（valueKey1,new String[]{"1258","2256","1154","3365","1158","2256"}）
//			map（valueKey2,new String[]{"1258","1154","3365","2256","1158","2256"}）		//由于有多组
//			map（barNames,new String[]{"机票金额","酒店金额"}）			//每组图名称


    /**
     * 创建一个柱状图
     *
     * @param dataset    生成chart的数据
     * @param chartTitle 图表标题
     * @param Xname      X轴名称
     * @param Yname      Y轴名称
     * @return
     */
    public static JFreeChart createBarChart(DefaultCategoryDataset dataset, String chartTitle, String Xname, String Yname) {
        JFreeChart chart = ChartFactory.createBarChart(chartTitle, Xname, Yname, dataset, PlotOrientation.VERTICAL, true, true, false);
        //设置柱状图基本样式
        CategoryPlot categoryplot = chart.getCategoryPlot();//获得图表对象
        // 图例字体清晰
        chart.getLegend().setItemFont(new Font("宋体", Font.ROMAN_BASELINE, 12));
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        chart.setTextAntiAlias(false);
        // 2 ．2 主标题对象 主标题对象是 TextTitle 类型
        chart.setTitle(new TextTitle(chartTitle, new Font("宋体", Font.BOLD, 15)));
        // 2 ．2.1:设置中文
        // x,y轴坐标字体
        Font labelFont = new Font("宋体", Font.TRUETYPE_FONT, 12);

        //Y轴数据设置
        NumberAxis vn = (NumberAxis) categoryplot.getRangeAxis();

        //X轴坐标的显示中文   domainAxis是X轴
        CategoryAxis domainAxis = categoryplot.getDomainAxis();
        domainAxis.setLabelFont(labelFont);// 轴标题
        domainAxis.setTickLabelFont(labelFont);// 轴数值

        // y轴设置
        ValueAxis rangeAxis = categoryplot.getRangeAxis();
        rangeAxis.setLabelFont(labelFont); //轴标题
        rangeAxis.setTickLabelFont(labelFont);//轴数值

        //右侧Y轴
        NumberAxis numberaxis = new NumberAxis("折扣");
        categoryplot.setRangeAxis(1, numberaxis);

        chart.getPlot().setBackgroundPaint(Color.WHITE);// 去掉背景色
        CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.075);// 设置柱子最大宽度
        renderer.setShadowVisible(true);//是否显示阴影
        BarRenderer customBarRenderer = (BarRenderer) plot.getRenderer();
        customBarRenderer.setSeriesPaint(0, Color.decode("#1BB2D8")); // 给series1 Bar
        customBarRenderer.setSeriesPaint(1, Color.decode("#1790CF")); // 给series2 Bar
        customBarRenderer.setSeriesPaint(2, Color.decode("#88B0BB")); // 给series3 Bar
        customBarRenderer.setSeriesPaint(3, Color.decode("#88B0BB")); // 给series4 Bar
        customBarRenderer.setSeriesPaint(4, Color.decode("#1C7099")); // 给series5 Bar
        customBarRenderer.setSeriesPaint(5, Color.decode("#75ABD0")); // 给series6 Bar
        //设置横轴的label为45度(由于中文长度可能过长展示不下)
        CategoryAxis categoryaxis = categoryplot.getDomainAxis();
        categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        //画折线图
        LineAndShapeRenderer lineandshaperenderer = new LineAndShapeRenderer();
        lineandshaperenderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        categoryplot.setRenderer(1, lineandshaperenderer);
        return chart;
    }

    /**
     * 获取生成chart所需数据
     *
     * @param map
     * @param typeKey  横坐标中文对应的Key
     * @param valueKey 数值对应的Key。数据格式 Double[]
     * @param barNames
     * @return
     */
    public static DefaultCategoryDataset createBarChart(Map map, String typeKey, String[] valueKey, String[] barNames) {
        String[] categories = (String[]) map.get(typeKey);
        List<Serie> series = new ArrayList<Serie>();
        for (int i = 0; i < valueKey.length; i++) {
            series.add(new Serie(barNames[i], (Double[]) map.get(valueKey[i])));
        }
        DefaultCategoryDataset dataset = createDefaultCategoryDataset(series, categories);
        return dataset;
    }


    /**
     * 在已创建好的柱状图上加上折线图
     *
     * @param barChart         柱状图chart实体bean
     * @param lineChartDataset 折线图需要数据
     */
    public static void addLineChartToBar(JFreeChart barChart, DefaultCategoryDataset lineChartDataset) {
        CategoryPlot categoryplot = barChart.getCategoryPlot();//获得图表对象
        //0显示是柱状图，1显示折线图
        categoryplot.setDataset(1, lineChartDataset);
        //显示折线图
        categoryplot.mapDatasetToRangeAxis(1, 1);            //折线、柱状图分别对应左右Y轴
    }

    /**
     * 创建类别数据集合
     */
    public static DefaultCategoryDataset createDefaultCategoryDataset(List<Serie> series, String[] categories) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Serie serie : series) {
            String name = serie.getName();
            List<Object> data = serie.getData();
            if (data != null && categories != null && data.size() == categories.length) {
                for (int index = 0; index < data.size(); index++) {
                    String value = data.get(index) == null ? "" : data.get(index).toString();
                    if (isPercent(value)) {
                        value = value.substring(0, value.length() - 1);
                    }
                    if (isNumber(value)) {
                        dataset.setValue(Double.parseDouble(value), name, categories[index]);
                    }
                }
            }

        }
        return dataset;

    }

    /**
     * 是不是一个%形式的百分比
     *
     * @param str
     * @return
     */
    public static boolean isPercent(String str) {
        return str != null ? str.endsWith("%") && isNumber(str.substring(0, str.length() - 1)) : false;
    }

    /**
     * 是不是一个数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        return str != null ? str.matches("^[-+]?(([0-9]+)((([.]{0})([0-9]*))|(([.]{1})([0-9]+))))$") : false;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        //创建饼状图：
        List<Map> pieList = new ArrayList<Map>();
        for (int i = 1; i < 6; i++) {
            Map m = new HashMap();
            m.put("TYPE", "类别" + i);
            m.put("JE", DateUtil.getNo(3) + i);
            pieList.add(m);
        }
        ChartParam pieChartParam = new ChartParam();
        pieChartParam.setPieList(pieList);
        pieChartParam.setTypeKey("TYPE");
        pieChartParam.setPieValueKey("JE");
        pieChartParam.setChartTitle("饼图实例");
        pieChartParam.setChartType("pie");
        pieChartParam.setWidth(300);
        pieChartParam.setHeight(300);
        //创建折线柱状混合图
        Map m = new HashMap();
        m.put("TYPE", new String[]{"一月", "二月", "三月", "四月", "五月", "六月"});
        m.put("JPJE", new Double[]{1168.56, 1568.00, 2562.00, 1569.00, 3654.00, 4158.00});
        m.put("JDJE", new Double[]{1168.56, 5162.00, 9654.00, 225.00, 758.00, 1527.00});
        m.put("HCPJE", new Double[]{6621.56, 1125.00, 2256.00, 6541.00, 1758.00, 1627.00});
        String[] valueKye = new String[]{"JPJE", "JDJE", "HCPJE"};
        String[] barName = new String[]{"机票", "酒店", "火车票"};

        Map m1 = new HashMap();
        m1.put("TYPE", new String[]{"一月", "二月", "三月", "四月", "五月", "六月"});
        m1.put("JPZK", new Double[]{0.56, 0.00, 0.18, 0.32, 0.95, 0.14});
        m1.put("HCPZK", new Double[]{0.12, 0.27, 0.36, 0.69, 0.18, 0.55});
        String[] valueKye1 = new String[]{"JPZK", "HCPZK"};
        String[] barName1 = new String[]{"机票平均折扣", "酒店平均折扣"};

        ChartParam mutilChartParam = new ChartParam();
        mutilChartParam.setChartType("mutil");
        mutilChartParam.setxName("月份");
        mutilChartParam.setyName("金额");
        mutilChartParam.setTypeKey("TYPE");
        mutilChartParam.setBarMap(m);
        mutilChartParam.setBarName(barName);
        mutilChartParam.setBarValueKey(valueKye);
        mutilChartParam.setLineMap(m1);
        mutilChartParam.setLineName(barName1);
        mutilChartParam.setLineValueKey(valueKye1);
        mutilChartParam.setWidth(500);
        mutilChartParam.setHeight(300);
        mutilChartParam.setChartTitle("混合图实例");

        try {
//			createChartPng("E:\\chart\\31.png",pieChartParam);
//			createChartPng("E:\\chart\\32.png",mutilChartParam);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
