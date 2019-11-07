package com.github.app.util.code.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块中表的定义
 */
public class CodeTableConf {
    // 表名
    private String name;
    // 表前缀
    private String prefix;
    // 生成的实体类名
    private String entityName;
    //  如果是主从表，则从表需设置该属性，表示父表的关联属性
    private String parentField;
    // 表关联类型，分为OneToOne,OneToMany等，如果与父表的关系是多对一，则会在父表对应的 entity中生成该子表的List集合属性
    private String refType;
    // 子表列表，即一对多的子表
    private List<CodeTableConf> subTables = new ArrayList<CodeTableConf>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<CodeTableConf> getSubTables() {
        return subTables;
    }

    public void setSubTables(List<CodeTableConf> subTables) {
        this.subTables = subTables;
    }

    public String getParentField() {
        return parentField;
    }

    public void setParentField(String parentField) {
        this.parentField = parentField;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }
}
