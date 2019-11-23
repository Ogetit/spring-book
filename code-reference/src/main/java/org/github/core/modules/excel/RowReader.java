package org.github.core.modules.excel;

import java.util.List;
/**
 * 测试例子
 * @author 章磊
 *
 */
public class RowReader  implements IRowReader{
	@Override
	public void getRows(int sheetIndex, int curRow, List<Object> rowlist) {
		 System.out.println(sheetIndex+"="+curRow+"="+rowlist.size()+"="+rowlist);
	}

	@Override
	public Object getCol(int sheetIndex, int curRow, int curCol, String title, String value) {
 		System.out.println(sheetIndex+"="+curRow+"="+curCol+"="+title+"="+value);
		return value;
	}  

}
