package org.github.core.modules.export.utils;

import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * 注意：数据量大的地方不可用此方法(1万条数据以上，请勿使用)
 * Created by github on 2016/11/21.
 */
public class ExcelExportUtils {


    /**
     *  直接输出文件到下载，不需要存临时文件
     * @param mbFile 模板文件
     * @param response  输出流
     * @param downFileName  显示在下载提示中的文件名
     * @param beans 数据对象
     * @throws Exception
     */
    public static void transformXLS(File mbFile, HttpServletResponse response, String downFileName, Map beans) throws Exception {
        if (mbFile == null || !mbFile.exists()) {
            throw new Exception("模板文件不存在,请配置模板文件！");
        }
        response.setContentType("application/x-excel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + downFileName);
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(mbFile));
        XLSTransformer transformer = new XLSTransformer();
        Workbook workbook = transformer.transformXLS(is, beans);
        BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream());
        workbook.write(os);
        is.close();
        os.flush();
        os.close();
    }


    /**
     * excel模板导出，el表达式
     * @param mbFile 模板文件
     * @param exportFile 输出的目标文件
     * @param beans 数据对象
     * @throws Exception
     */
    public static void transformXLS(File mbFile, File exportFile, Map beans) throws Exception {
        if (mbFile == null || !mbFile.exists()) {
            throw new Exception("模板文件不存在,请配置模板文件！");
        }
        File parent = exportFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        XLSTransformer transformer = new XLSTransformer();
        transformer.transformXLS(mbFile.getAbsolutePath(), beans, exportFile.getAbsolutePath());
    }

}
