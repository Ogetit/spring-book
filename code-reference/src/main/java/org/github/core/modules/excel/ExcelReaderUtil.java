package org.github.core.modules.excel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author 章磊
 *
 */
public class ExcelReaderUtil {
	// excel2003扩展名
	public static final String EXCEL03_EXTENSION = ".xls";
	// excel2007扩展名
	public static final String EXCEL07_EXTENSION = ".xlsx";

	/**
	 * 读取Excel文件，可能是03也可能是07版本
	 * @param reader
	 * @param startRow
	 * @param fileName
	 * @param cls
	 * @throws Exception
	 */
	public static void readExcel(IRowReader reader,int startRow, String fileName,Class cls) throws Exception {
		readExcel(reader,startRow,0,fileName,cls);
	}

	/**
	 * 读取Excel文件，可能是03也可能是07版本
	 * @param reader
	 * @param startRow
	 * @param titleRow
	 * @param fileName
	 * @param cls
	 * @throws Exception
	 */
	public static void readExcel(IRowReader reader,int startRow,int titleRow, String fileName,Class cls) throws Exception {
		readExcel(reader, startRow, titleRow, fileName, cls, null);
	}

	/**
	 * /**
	 * 读取Excel文件，可能是03也可能是07版本;
	 * @param reader
	 * @param startRow
	 * @param titleRow
	 * @param fileName
	 * @param cls
	 * @param zdSheet 指定sheet下标，传下标后则只读该下标的sheet;sheet下标从0开始
	 * @throws Exception
	 */
	public static void readExcel(IRowReader reader,int startRow,int titleRow, String fileName,Class cls,String zdSheet) throws Exception {
		readExcel(reader,startRow,titleRow,new FileInputStream(new File(fileName)),fileName,cls,zdSheet);
	}
	/**
	 * /**
	 * 读取Excel文件，可能是03也可能是07版本;
	 * @param reader
	 * @param startRow
	 * @param titleRow
	 * @param file
	 * @param cls
	 * @param zdSheet 指定sheet下标，传下标后则只读该下标的sheet;sheet下标从0开始
	 * @throws Exception
	 */
	public static void readExcel(IRowReader reader, int startRow, int titleRow, File file, Class cls, String zdSheet) throws Exception {
		String fileName=file.getName();
		readExcel(reader,startRow,titleRow,new FileInputStream(file),fileName,cls,zdSheet);
	}

	/**
	 *
	 * @param reader
	 * @param startRow
	 * @param titleRow
	 * @param inputStream
	 * @param fileExt  xls 或 xlsx  文件的扩展名
	 * @param cls
	 * @param zdSheet
	 * @throws Exception
	 */
	public static void readExcel(IRowReader reader, int startRow, int titleRow, InputStream inputStream,String fileExt, Class cls, String zdSheet) throws Exception {
		// 处理excel2003文件
		if (StringUtils.trimToEmpty(fileExt).toLowerCase().endsWith(EXCEL03_EXTENSION)) {
			Excel2003Reader excel03 = new Excel2003Reader();
			excel03.setRowReader(reader);
			excel03.setCls(cls);
			excel03.setStartRow(startRow);
			excel03.setTitleRow(titleRow);
			excel03.setZdSheet(zdSheet);
			excel03.process(inputStream);
			// 处理excel2007文件
		} else if (StringUtils.trimToEmpty(fileExt).toLowerCase().endsWith(EXCEL07_EXTENSION)) {
			Excel2007Reader excel07 = new Excel2007Reader();
			excel07.setRowReader(reader);
			excel07.setCls(cls);
			excel07.setStartRow(startRow);
			excel07.setTitleRow(titleRow);
			if(StringUtils.isNotBlank(zdSheet)){
				excel07.processOneSheet(inputStream, NumberUtils.toInt(zdSheet)+1);
			}else {
				excel07.process(inputStream);
			}
		} else {
			throw new Exception("文件格式错误，fileName的扩展名只能是xls或xlsx。");
		}
	}
	public static void main(String []args) throws Exception{
		long ss = System.currentTimeMillis();
		readExcel(new RowReader(),0, 5,"D:/1234.xls",null,"0");
		//System.out.println(System.currentTimeMillis()-ss);
	}
}
