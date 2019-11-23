package org.github.core.modules.excel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.github.core.modules.export.ExportPage;
import org.github.core.modules.service.SpringContextUtil;
import org.github.core.modules.utils.Arith;
import org.github.core.modules.utils.DateUtil;
import org.github.core.modules.utils.WebUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by java on 2016/11/1.
 */
public class ExportCsv {
    private static int PECOUNT = 5000;//每次导出5000条
    private static int CSVBECOUNT = 1000000;//csv导出最大条数
    private static String ENCODE="GBK";
    private int rownum = 1;//序号计数

    private boolean stop;//是否终止

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public int getRownum() {
        return rownum;
    }

    /**
     * 导出csv
     */
    public void exportCsv(Collection data, String titles, String pathFile) throws Exception {
        String[] titleArr = StringUtils.split(titles, ";");
        List<String> colNames = new ArrayList<String>();
        List<String> fields = new ArrayList<String>();
        initTitle(titleArr, colNames, fields);

        Map<Integer, Number> sumMap = new HashMap<Integer, Number>();

        String path = WebUtils.getRootPath(pathFile);
        File file = new File(path);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        int rownum = 1;//序号计数
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file);
            //写入表头
            ExportCsv.addCsvHead(pw, colNames);
            if (CollectionUtils.isNotEmpty(data)) {
                Iterator<?> itr = data.iterator();
                StringBuffer rowStrs = new StringBuffer();
                while (itr.hasNext()) {
                    if (rownum > CSVBECOUNT) {
                        break;
                    }
                    Object one = itr.next();
                    //按照csv格式组装一行数据
                    ExportCsv.addCsvRow(rowStrs, one, fields, sumMap, rownum++);
                }
                pw.print(rowStrs);//写入CSV文本
                ExportCsv.addCsvFoot(pw, sumMap, colNames);
            }
        } finally {
            IOUtils.closeQuietly(pw);
        }
    }

    /**
     * 导出csv
     *
     * @param exportPageClass
     * @param serachParam
     * @throws Exception
     */
    public void exportCsv(Class<? extends ExportPage> exportPageClass, Object serachParam, String titles, String pathFile) throws Exception {
        ExportPage exportPage = SpringContextUtil.getBean(exportPageClass);
        String[] titleArr = StringUtils.split(titles, ";");
        List<String> colNames = new ArrayList<String>();
        List<String> fields = new ArrayList<String>();
        initTitle(titleArr, colNames, fields);
        Map<Integer, Number> sumMap = new HashMap<Integer, Number>();

        Object serchParam = serachParam;
        String path = WebUtils.getFileRootPath(pathFile);
        File file = new File(path);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(file,ENCODE);
            //写入表头
            ExportCsv.addCsvHead(pw, colNames);
            Collection<?> data = null;
            int pageNum = 1;
            data = exportPage.getCollection(serchParam, (pageNum-1)*PECOUNT, PECOUNT);
            while (true) {
                if (isStop()) {//任务被终止
                    return;
                }
                if (CollectionUtils.isNotEmpty(data)) {
                    Iterator<?> itr = data.iterator();
                    StringBuffer rowStrs = new StringBuffer();
                    while (itr.hasNext()) {
                        if (rownum > CSVBECOUNT) {
                            break;
                        }
                        Object one = itr.next();
                        //导出前特殊处理
                        exportPage.beforeExport(one);
                        //按照csv格式组装一行数据
                        ExportCsv.addCsvRow(rowStrs, one, fields, sumMap, rownum++);
                    }
                    pw.print(rowStrs);//写入CSV文本
                } else {
                    break;
                }
                if (data.size() != PECOUNT) {
                    break;
                }
                if (isStop()) {//任务被终止
                    return;
                }
                pageNum++;
                data = exportPage.getCollection(serchParam, (pageNum-1)*PECOUNT, PECOUNT);
            }
            if (rownum > 1) {
                ExportCsv.addCsvFoot(pw, sumMap, colNames);
            }
        } finally {
            IOUtils.closeQuietly(pw);
        }
    }

    private void initTitle(String[] titleArr, List<String> colNames, List<String> fields) {
        for (String title : titleArr) {
            int last = title.lastIndexOf(",");
            if (last < 0) {
                fields.add(title);
            } else {
                fields.add(title.substring(0, last));
            }
            if (last >= title.length() - 1) {
                colNames.add(title);
            } else {
                colNames.add(title.substring(last + 1));
            }
        }
    }

    /**
     * 添加表头
     */
    public static void addCsvHead(PrintWriter pw, List<String> colNames) {
        StringBuffer sb = new StringBuffer("序号,");
        for (String title : colNames) {
            sb.append(title + ",");
        }
        pw.println(sb);
    }

    /**
     * @param sb
     * @param rowMap
     * @param fields
     * @param sumMap
     * @param rownum
     */
    private static void addCsvRow(StringBuffer sb, Object rowMap, List<String> fields, Map<Integer, Number> sumMap, int rownum) {
        sb.append("" + rownum + ",");
        Integer columnIndex = 0;
        for (String colname : fields) {
            try {
                Object o_value = ExportJexlUtil.evaluateVal(rowMap, colname);
                if (null != o_value) {
                    if (o_value instanceof String || o_value instanceof StringBuffer) {
                        // 特殊字符处理 回车 替换为 空格 逗号替换为大写逗号
                        String o_value_s = o_value.toString();
                        o_value_s = o_value_s.replaceAll("\n", "	").replaceAll("\r", "	").replaceAll(",", "，  ")
                                .replaceAll("\"", "“");
                        if (NumberUtils.isDigits(o_value_s) && (o_value_s.length() > 13 || (o_value_s.startsWith("0") && o_value_s.length()>1))) {
                            sb.append("\t" + o_value_s + ",");
                        } else if (o_value_s.indexOf("/") >= 0 && countStr(o_value_s, "/") > 1) {// 防止/excel自动转为月份了
                            sb.append("\t" + o_value_s + ",");
                            //日期有2个 “/”   乘机人有一个‘/’ 乘机人不处理
                        } else {
                            String reg = "^\\d+[eE]\\d*$";//文本型科学计数，如客户名称为8000E2，导出会以科学计数显示，需要加”\t“处理
                            //String regcsz = "\\d{5,}";//纯数字数据，当大于5位是加“\t”，避免被科学计数
                            if (o_value.toString().matches(reg)) {
                                sb.append("" + o_value_s + "\t,");
                            } else {
                                sb.append("" + o_value_s + ",");
                            }
                        }
                    } else if (o_value instanceof Number) {
                        sb.append("" + o_value + ",");
                        // 合计
                        Number n = (Number) sumMap.get(columnIndex);
                        if (n == null) {
                            n = 0;
                        }
                        n = Arith.add(n.doubleValue(), ((Number) o_value).doubleValue());
                        sumMap.put(columnIndex, n);
                    } else if(o_value instanceof Date) {
                        String o_value_s = DateUtil.dateToStrLong((Date) o_value);
                        sb.append("" + o_value_s + "\t,");
                    } else {
                        String o_value_s = o_value.toString();
                        sb.append("" + o_value_s + ",");
                    }
                } else {
                    sb.append("" + "" + ",");
                }
            } catch (Exception e) {
                sb.append("" + "" + ",");
                e.printStackTrace();
            }
            columnIndex++;
        }
        sb.append("\n");
    }

    /**
     * 合计
     */
    private static void addCsvFoot(PrintWriter pw, Map<Integer, Number> sumMap, List<String> colNames) {
        if (sumMap.isEmpty()) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("合计,");
        Integer columnIndex = 0;
        for (String title : colNames) {
            Number o = sumMap.get(columnIndex);
            if (o == null) {
                sb.append(",");
            } else {
                sb.append("" + Arith.round(o.doubleValue(), 2) + ",");
            }
            columnIndex++;
        }
        pw.print(sb.toString());
    }

    private static int countStr(String str1, String str2) {
        int counter = 0;
        if (str1.indexOf(str2) == -1) {
            return 0;
        }
        while (str1.indexOf(str2) != -1) {
            counter++;
            str1 = str1.substring(str1.indexOf(str2) + str2.length());
        }
        return counter;
    }
}
