package org.github.core.modules.excel;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.github.core.modules.export.ExportPage;
import org.github.core.modules.mapper.JsonMapper;
import org.github.core.modules.service.SpringContextUtil;
import org.github.core.modules.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * POI工具导出Excel，只能导出集合中是Bean的数据。 支持单独按照集合导出和通过数据库后台翻页导出
 * <p>
 * 导出的模板是bean中定义的,不需要再写模板文件
 *
 * @author java
 */
public class ExportXls {
    private static final Logger logger = LoggerFactory.getLogger(ExportXls.class);

    private static int PECOUNT = 10000;// 每次导出10000条

    public static int EXCELBECOUNT = 300000;// excel导出最大条数

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
     * 单个集合写入
     *
     * @param list
     * @param out  输出流
     * @throws Exception
     */
    public void exportList(Collection<?> list, OutputStream out) throws Exception {
        exportList(new Collection[]{list}, out);
    }

    /**
     * 多个集合写入
     *
     * @param list
     * @param out  输出流
     * @throws Exception
     */
    public void exportList(Collection<?>[] list, OutputStream out) throws Exception {
        long t = System.currentTimeMillis();
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int i = 0; i < list.length; i++) {
            exportList(list[i], workbook);
        }
        workbook.write(out);
        logger.info("导出Excel写流完成耗时" + (System.currentTimeMillis() - t));
    }

    /**
     * 单个集合写入
     *
     * @param list
     * @param pathFile 相对路径目的文件 如果文件存在则在这个文件上添加一个新的sheet
     * @throws Exception
     */
    public void exportList(Collection<?> list, String pathFile) throws Exception {
        exportList(new Collection[]{list}, pathFile);
    }

    /**
     * 多个集合写入
     *
     * @param list
     * @param pathFile 相对路径目的文件 如果文件存在则在这个文件上添加一个新的sheet
     * @throws Exception
     */
    public void exportList(Collection<?>[] list, String pathFile) throws Exception {
        long t = System.currentTimeMillis();
        // 声明一个工作薄
        HSSFWorkbook workbook = ExportXlsUtil.getWorkbook(pathFile);
        for (int i = 0; i < list.length; i++) {
            exportList(list[i], workbook);
        }
        ExportXlsUtil.writeFile(pathFile, workbook);
        logger.info("导出Excel写文件完成耗时" + (System.currentTimeMillis() - t));
    }


    /**
     * 一个excel文件，多个sheet页的模板,返回模板文件的相对路径
     *
     * @param objects
     */
    public static String createTemplate(Object[] objects) throws Exception {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        String filename = DateUtil.getNo(3);
        for (int i = 0; i < objects.length; i++) {
            Map<ExcelTitleBean, ExcelGetValue> fieldMap = null;
            Object first = objects[i];
            try {
                fieldMap = ExcelFieldUtil.getFields(first);
            } catch (Exception e) {
                logger.error("解析导出Bean的属性失败",e);
                return null;
            }
            if (fieldMap == null || fieldMap.size() < 1) {
                logger.error(first.getClass().getName() + "翻页导出Bean的属性失败,检查是不是有ExcelTitle注释");
                return null;
            }
            if (i == 0) {
                String title = ExcelFieldUtil.getClassTitle(first);
                if (StringUtils.isNotBlank(title)) {
                    filename = title;
                }
            }
            ExportXlsUtil.createHead(fieldMap, first, workbook);
        }
        String pathFile = "/updownload/tmp/export/Template/" + filename + ".xls";
        ExportXlsUtil.writeFile(pathFile, workbook);
        return pathFile;
    }

    /**
     * 数据库翻页获取数据导出
     *
     * @param exportPageClass
     * @param serachParam
     * @param pathFile        相对路径目的文件 如果文件存在则在这个文件上添加一个新的sheet
     * @throws Exception
     */
    public void exportPage(Class<? extends ExportPage> exportPageClass, Object serachParam, String pathFile) throws Exception {
        exportPage(new Class[]{exportPageClass}, new Object[]{serachParam}, pathFile);
    }

    /**
     * 数据库翻页获取数据后再导出，支持同一个excel多个sheet导出
     *
     * @param exportPageClass
     * @param serachParam
     * @param pathFile        相对路径目的文件 如果文件存在则在这个文件上添加一个新的sheet
     * @throws Exception
     */
    public void exportPage(Class<? extends ExportPage>[] exportPageClass, Object[] serachParam, String pathFile) throws Exception {
        long t = System.currentTimeMillis();
        // 声明一个工作薄
        HSSFWorkbook workbook = ExportXlsUtil.getWorkbook(pathFile);
        for (int i = 0; i < exportPageClass.length; i++) {
            exportPage(exportPageClass[i], serachParam[i], workbook);
        }
        ExportXlsUtil.writeFile(pathFile, workbook);
        logger.info("翻页导出Excel写文件完成耗时" + (System.currentTimeMillis() - t));
    }

    private void exportPage(Class<? extends ExportPage> exportPageClass, Object serachParam, HSSFWorkbook workbook) throws Exception {
        logger.info(exportPageClass.getName() + "翻页导出Excel开始，查询条件" + JsonMapper.nonDefaultMapper().toJson(serachParam));
        long tall = System.currentTimeMillis();
        int pageNum = 1;
        int allCount = 0;
        ExportPage exportPage = SpringContextUtil.getBean(exportPageClass);

        // 第一次在这里执行是为了获取到表格头的class对象
        long t = System.currentTimeMillis();
        Collection<?> list = exportPage.getCollection(serachParam, (pageNum - 1) * PECOUNT, PECOUNT);
        if (list == null || list.size() < 1) {
            return;
        }

        Object first = CollectionUtils.get(list, 0);
        if (first == null) {
            logger.error(exportPageClass.getName() + "翻页获取导出的集合对象失败");
            return;
        }
        Map<ExcelTitleBean, ExcelGetValue> fieldMap = null;
        try {
            fieldMap = ExcelFieldUtil.getFields(first);
        } catch (Exception e) {
            logger.error(exportPageClass.getName() + "翻页解析导出Bean的属性失败",e);
            return;
        }
        if (fieldMap == null || fieldMap.size() < 1) {
            logger.error(exportPageClass.getName() + "翻页导出Bean的属性失败,检查是不是有ExcelTitle注释");
            return;
        }
        HSSFSheet sheet = ExportXlsUtil.createHead(fieldMap, first, workbook);
        // 数据行单元格样式
        HSSFCellStyle dataRowCellStyle = ExportXlsUtil.getDataRowCellStyle(workbook);

        do {
            if (list == null || list.size() < 1) {
                break;
            }
            logger.info(exportPageClass.getName() + "翻页导出Excel，第" + (pageNum - 1) + "页获取数据条数" + list.size() + "耗时" + (System.currentTimeMillis() - t));
            allCount += list.size();
            rownum = allCount;
            for (Object o : list) {
                exportPage.beforeExport(o);
            }
            long et = System.currentTimeMillis();
            ExportXlsUtil.createRowList(list, fieldMap, sheet, dataRowCellStyle);
            logger.info(exportPageClass.getName() + "翻页导出Excel，生成Excel表格 耗时" + (System.currentTimeMillis() - et));
            if (list.size() != PECOUNT) {
                break;
            }
            if (allCount > EXCELBECOUNT) {
                break;
            }
            t = System.currentTimeMillis();
            pageNum++;
            list = exportPage.getCollection(serachParam, (pageNum - 1) * PECOUNT, PECOUNT);
        } while (true);

        logger.info(exportPageClass.getName() + "翻页导出Excel，最大条数限制" + EXCELBECOUNT + "完成条数" + allCount + "耗时" + (System.currentTimeMillis() - tall));
    }

    private void exportList(Collection<?> list, HSSFWorkbook workbook) {
        if (list == null || list.size() < 1) {
            return;
        }
        long tall = System.currentTimeMillis();
        logger.info("导出Excel开始,记录条数" + list.size());
        rownum += list.size();
        Object first = CollectionUtils.get(list, 0);
        if (first == null) {
            logger.error("获取导出的集合对象失败");
            return;
        }
        Map<ExcelTitleBean, ExcelGetValue> fieldMap = null;
        try {
            fieldMap = ExcelFieldUtil.getFields(first);
        } catch (Exception e) {
            logger.error("解析导出Bean的属性失败",e);
            return;
        }
        if (fieldMap == null || fieldMap.size() < 1) {
            logger.error("导出Bean的属性失败,检查是不是有ExcelTitle注释");
            return;
        }
        HSSFSheet sheet = ExportXlsUtil.createHead(fieldMap, first, workbook);
        // 数据行单元格样式
        HSSFCellStyle dataRowCellStyle = ExportXlsUtil.getDataRowCellStyle(workbook);

        ExportXlsUtil.createRowList(list, fieldMap, sheet, dataRowCellStyle);
        logger.info("导出Excel完成记录条数" + list.size() + "耗时" + (System.currentTimeMillis() - tall));
    }


}
