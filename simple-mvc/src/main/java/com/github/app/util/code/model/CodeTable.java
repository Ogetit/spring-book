package com.github.app.util.code.model;

import java.util.ArrayList;
import java.util.List;

public class CodeTable {
    private CodeModule module;
    private String packageName;
    // 完整表名
    private String tableFullName;
    // 表名，去掉prefix
    private String tableName;
    // 实体名
    private String entityName;
    // 完整实体类名
    private String entityCamelName;
    // 表注释
    private String remark;
    // 主键
    private String primaryKey;
    // 联合主键
    private List<String> primaryKeys;
    // 主键属性名
    private String primaryProperty;
    // 主键属性类型
    private String primaryPropertyType;
    private String primaryCamelProperty;
    // 主键类型
    private String primaryKeyType;
    private List<CodeColumn> primaryKeyList;
    // 若为子表，则此属性为上级表的属性
    private String parentProperty;

    private List<String> importClassList = new ArrayList<String>();
    private List<CodeColumn> columns = new ArrayList<CodeColumn>();
    private List<CodeTable> subTables = new ArrayList<CodeTable>();
    // 表间关联类型
    private String refType;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<CodeColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<CodeColumn> columns) {
        this.columns = columns;
    }

    public List<String> getImportClassList() {
        return importClassList;
    }

    public void setImportClassList(List<String> importClassList) {
        this.importClassList = importClassList;
    }

    public String getEntityCamelName() {
        return entityCamelName;
    }

    public void setEntityCamelName(String entityCamelName) {
        this.entityCamelName = entityCamelName;
    }

    public String getPrimaryKeyType() {
        return primaryKeyType;
    }

    public void setPrimaryKeyType(String primaryKeyType) {
        this.primaryKeyType = primaryKeyType;
    }

    public String getPrimaryProperty() {
        return primaryProperty;
    }

    public void setPrimaryProperty(String primaryProperty) {
        this.primaryProperty = primaryProperty;
    }

    public String getPrimaryCamelProperty() {
        return primaryCamelProperty;
    }

    public void setPrimaryCamelProperty(String primaryCamelProperty) {
        this.primaryCamelProperty = primaryCamelProperty;
    }

    public String getPrimaryPropertyType() {
        return primaryPropertyType;
    }

    public void setPrimaryPropertyType(String primaryPropertyType) {
        this.primaryPropertyType = primaryPropertyType;
    }

    public CodeModule getModule() {
        return module;
    }

    public void setModule(CodeModule module) {
        this.module = module;
    }

    public String getTableFullName() {
        return tableFullName;
    }

    public void setTableFullName(String tableFullName) {
        this.tableFullName = tableFullName;
    }

    public List<CodeTable> getSubTables() {
        return subTables;
    }

    public void setSubTables(List<CodeTable> subTables) {
        this.subTables = subTables;
    }

    public String getParentProperty() {
        return parentProperty;
    }

    public void setParentProperty(String parentProperty) {
        this.parentProperty = parentProperty;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public List<CodeColumn> getPrimaryKeyList() {
        return primaryKeyList;
    }

    public void setPrimaryKeyList(List<CodeColumn> primaryKeyList) {
        this.primaryKeyList = primaryKeyList;
    }
}
