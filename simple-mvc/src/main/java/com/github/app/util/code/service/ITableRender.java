/**
 * 
 */
package com.github.app.util.code.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.app.util.code.CodeConfig;
import com.github.app.util.code.model.CodeModule;
import com.github.app.util.code.model.CodeTable;
import com.github.app.util.code.model.CodeTableConf;

/**
 * 获取数据表和数据字段信息接口
 *
 */
public interface ITableRender {
	
	void setConfig(CodeConfig config);
	
	/**
     * 连接数据库获取所有表信息 
     */  
	List<CodeTableConf> getAllTables(String pattern);
	
	 /**
     * 获取指定表信息并封装成Table对象 
     * @param tbConf 
     * @param module
     * @param con 
     */
	 CodeTable getTable(CodeTableConf tbConf, CodeModule module, Connection con) throws SQLException;

	/**
     * 获取数据表的所有字段
     * @param table
     * @param conn
     * @throws SQLException
     */
	void fillTableColumns(CodeTable table, Connection conn) throws SQLException;

	/**
	 * 获取表主键
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	String getTablePrimaryKey(String tableName, Connection con) throws SQLException;
	/**
	 * 取表的联合主键
	 * @param tableName
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	List<String> getTablePrimaryKeys(String tableName, Connection con) throws SQLException;

	/**
	 * 获取字段类型
	 * @param table
	 * @param column 指定列名
	 * @return
	 * @throws SQLException
	 */
	String getColumnType(CodeTable table, String column) throws SQLException;
	
	/**
	 * 表注释
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	String getTableRemark(String tableName, Connection con) throws SQLException;
}
