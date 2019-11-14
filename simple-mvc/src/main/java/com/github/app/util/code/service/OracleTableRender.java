package com.github.app.util.code.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.app.util.code.CodeConfig;
import com.github.app.util.code.model.CodeColumn;
import com.github.app.util.code.model.CodeModule;
import com.github.app.util.code.model.CodeTable;
import com.github.app.util.code.model.CodeTableConf;
import com.github.app.util.code.util.CodeUtil;

public class OracleTableRender implements ITableRender {

    private CodeConfig config;

    @Override
    public void setConfig(CodeConfig config) {
        this.config = config;
    }

    /*
     * 连接数据库获取所有表信息 
     */
    @Override
    public List<CodeTableConf> getAllTables(String pattern) {
        if (CodeUtil.isEmpty(pattern)) {
            pattern = "*";
        }
        List<CodeTableConf> tbConfList = new ArrayList<CodeTableConf>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(config.getDb().getDriver());
            con = DriverManager
                    .getConnection(config.getDb().getUrl(), config.getDb().getUser(), config.getDb().getPwd());
            // 获取所有表名  
            String showTablesSql = "";
            showTablesSql = "select table_name from user_tables where table_name like ? and owner=upper(?)";
            // ORACLE查询所有表格名称命令
            ps = con.prepareStatement(showTablesSql);
            ps.setString(1, pattern);
            ps.setString(2, config.getDb().getUser());
            rs = ps.executeQuery();

            // 循环生成所有表的表信息
            while (rs.next()) {
                if (rs.getString(1) == null) {
                    continue;
                }
                CodeTableConf cf = new CodeTableConf();
                cf.setName(rs.getString(1));
                tbConfList.add(cf);
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tbConfList;
    }

    /**
     * 获取指定表信息并封装成Table对象
     *
     * @param tbConf
     * @param module
     * @param con
     */
    @Override
    public CodeTable getTable(CodeTableConf tbConf, CodeModule module, Connection con) throws SQLException {
        String tableName = tbConf.getName();
        CodeTable table = new CodeTable();
        table.setTableFullName(tableName);
        table.setTableName(tableName);
        if (module.isDeleteTablePrefix() && !CodeUtil.isEmpty(tbConf.getPrefix())) {
            table.setTableName(tableName.toLowerCase().replaceFirst(tbConf.getPrefix().toLowerCase(), ""));
        }
        System.out.println(tbConf);

        //获取表各字段的信息
        fillTableColumns(table, con);
        // 表主键相关信息
        table.setPrimaryKey(getTablePrimaryKey(tableName, con));
        table.setPrimaryKeys(getTablePrimaryKeys(tableName, con));
        table.setPrimaryProperty(CodeUtil.convertToFirstLetterLowerCaseCamelCase(table.getPrimaryKey()));
        table.setPrimaryKeyType(getColumnType(table, table.getPrimaryKey()));
        String propertyType = CodeUtil.convertType(table.getPrimaryKeyType());
        if (null != table.getModule() && "hibernate".equals(table.getModule().getPersistance())) {
            propertyType = "Integer".equals(propertyType) ? "Long" : propertyType;
        }
        table.setPrimaryPropertyType(propertyType);
        table.setPrimaryCamelProperty(CodeUtil.convertToCamelCase(table.getPrimaryKey()));

        // 表信息
        table.setRemark(getTableRemark(tableName, con));
        table.setEntityCamelName(CodeUtil.isEmpty(tbConf.getEntityName()) ? CodeUtil.convertToCamelCase(table.getTableName()) : tbConf.getEntityName());
        table.setEntityName(CodeUtil.convertToFirstLetterLowerCaseCamelCase(table.getTableName()));
        // 设置子表的 entity 属性
        table.setModule(module);
        if (!tbConf.getSubTables().isEmpty()) {
            List<CodeTable> subTables = new ArrayList<CodeTable>();
            for (CodeTableConf tc : tbConf.getSubTables()) {
                CodeTable tb = getTable(tc, module, con);
                tb.setParentProperty(CodeUtil.convertToFirstLetterLowerCaseCamelCase(tc.getParentField()));
                tb.setRefType(tc.getRefType());
                subTables.add(tb);
            }
            table.setSubTables(subTables);
        }
        return table;
    }

    /**
     * 获取数据表的所有字段
     *
     * @param table
     * @param conn
     *
     * @throws SQLException
     */
    @Override
    public void fillTableColumns(CodeTable table, Connection conn) throws SQLException {
        List<String> primaryKeys = getTablePrimaryKeys(table.getTableFullName(), conn);
        //查询表主键
        List<String> priCols = new ArrayList<String>();
        String sql =
                "select cu.* from user_cons_columns cu, user_constraints au where cu.constraint_name = au"
                        + ".constraint_name and "
                        + " au.constraint_type = 'P' AND cu.table_name = upper(?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, table.getTableFullName());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            priCols.add(rs.getString("column_name"));
        }
        rs.close();
        ps.close();

        //查询所有字段
        sql = "SELECT USER_TAB_COLS.TABLE_NAME, USER_TAB_COLS.COLUMN_NAME , "
                + "USER_TAB_COLS.DATA_TYPE, "
                + "USER_TAB_COLS.DATA_LENGTH , "
                + " USER_TAB_COLS.NULLABLE, "
                + " USER_TAB_COLS.COLUMN_ID, "
                + " user_tab_cols.data_default,"
                + "    user_col_comments.comments "
                + "FROM USER_TAB_COLS  "
                + "inner join user_col_comments on "
                + " user_col_comments.TABLE_NAME=USER_TAB_COLS.TABLE_NAME "
                + "and user_col_comments.COLUMN_NAME=USER_TAB_COLS.COLUMN_NAME "
                + "where  USER_TAB_COLS.Table_Name=upper(?) ORDER BY COLUMN_ID ASC";
        ps = conn.prepareStatement(sql);
        ps.setString(1, table.getTableFullName());
        rs = ps.executeQuery();
        while (rs.next()) {
            CodeColumn col = new CodeColumn();
            String colName = rs.getString("column_name");
            col.setColumnName(colName);
            String type = rs.getString("data_type").toUpperCase();
            if (null != table.getModule() && "jdbc".equals(table.getModule().getPersistance())) {
                type = CodeUtil.convertJdbcType(type);
            }
            col.setColumnType(type);
            col.setPropertyName(CodeUtil.convertToFirstLetterLowerCaseCamelCase(colName));
            System.out.println(table.getTableFullName() + ":::" + colName + ":::" + col.getColumnType());
            String propertyType = CodeUtil.convertType(col.getColumnType());
            if (null != table.getModule() && "hibernate".equals(table.getModule().getPersistance())) {
                propertyType = "Integer".equals(propertyType) ? "Long" : propertyType;
            }
            col.setPropertyType(propertyType);
            col.setPropertyCamelName(CodeUtil.convertToCamelCase(colName));
            col.setLength(rs.getLong("data_length"));
            col.setNullable(rs.getString("nullable").equals("YES") || rs.getString("nullable").equals("Y"));
            col.setDefaultValue(rs.getString("data_default"));
            col.setRemark(rs.getString("comments"));

            for (String primaryKey : primaryKeys) {
                if (colName.equalsIgnoreCase(primaryKey)) {
                    col.setPrimaryKey(true);
                    break;
                }
            }

            // String colKey = rs.getString("column_key");
            // if (!isPrimaryKey(priCols,colKey)) {
            // 	 col.setPrimaryKey(true);
            // }
            if (col.getPropertyType().indexOf(".") != -1 && !CodeUtil
                    .existsType(table.getImportClassList(), col.getPropertyType())) {
                table.getImportClassList().add(col.getPropertyType());
            }
            table.getColumns().add(col);
        }
        rs.close();
        ps.close();

    }

    /**
     * 判断是否是主键
     *
     * @param priCols    主键列表
     * @param columnName 要判断的列名
     *
     * @return
     */
    private boolean isPrimaryKey(List<String> priCols, String columnName) {
        for (String pri : priCols) {
            if (pri.equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTablePrimaryKey(String tableName, Connection con) throws SQLException {
        List<String> keys = getTablePrimaryKeys(tableName, con);
        if (keys.size() > 0) {
            return keys.get(0);
        }
        return null;
    }

    @Override
    public List<String> getTablePrimaryKeys(String tableName, Connection con) throws SQLException {
        // DatabaseMetaData dbMeta = con.getMetaData();
        // ResultSet rs = dbMeta.getPrimaryKeys(null,null,tableName);
        List<String> keys = new ArrayList<String>();
        String sql = "select a.constraint_name,a.column_name"
                + " from user_cons_columns a, user_constraints b"
                + "  where a.constraint_name = b.constraint_name"
                + " and b.constraint_type = 'P'"
                + " and a.table_name = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, tableName.toUpperCase());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            keys.add(rs.getString("COLUMN_NAME"));
        }
        rs.close();
        stmt.close();
        return keys;
    }

    /**
     * 主键类型
     *
     * @param table
     * @param column 指定列名
     *
     * @return
     *
     * @throws SQLException
     */
    @Override
    public String getColumnType(CodeTable table, String column) throws SQLException {
        String colType = "";
        for (CodeColumn col : table.getColumns()) {
            if (col.getColumnName().equalsIgnoreCase(column)) {
                return col.getColumnType();
            }
        }
        return colType;
    }

    /**
     * 表注释
     *
     * @param tableName
     *
     * @return
     *
     * @throws SQLException
     */
    @Override
    public String getTableRemark(String tableName, Connection con) throws SQLException {
        String remark = "";
        String sql = "SELECT COMMENTS FROM USER_TAB_COMMENTS WHERE table_name=upper(?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tableName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            remark = rs.getString("comments");
        }
        rs.close();
        ps.close();
        return remark == null ? tableName : remark;
    }
}
