package ${basePackage}.${moduleName}.${daoPackage};

import org.springframework.stereotype.Repository;
<#--import org.springframework.beans.factory.annotation.Autowired;-->

import com.github.app.util.jdbc.JdbcBaseDao;
import ${basePackage}.${moduleName}.${entityPackage}.${entityCamelName};
import java.util.List;
<#if primaryKey??>
import java.util.HashMap;
import java.util.Map;
</#if>

/**
 * ${remark!} 对应 Dao 类
 */
<#if primaryPropertyType??>
<#assign type=primaryPropertyType>
<#assign type=type?replace("java.util.","")>
<#assign type=type?replace("java.math.","")>
</#if>
@Repository
public class ${entityCamelName}Dao extends JdbcBaseDao<${entityCamelName}> {
    /**
     * 通用查询方法
     */
    public List<${entityCamelName}> select(${entityCamelName} entity) {
        String sql = "SELECT * FROM ${tableFullName!} WHERE 1=1 ";
        <#if columns??>
        <#list columns as col>
        if (null != entity.get${col.propertyCamelName}()) {
            sql += " AND ${col.columnName}=:${col.propertyName}";
        }
        </#list>
        </#if>
        return super.queryForList(sql, entity);
    }
    /**
     * 通用单表插入方法
     */
    public void insert(${entityCamelName} entity) {
        String sql = "INSERT INTO ${tableFullName!} (\n"
            <#if columns??>
                <#list columns as col>
                + "${col.columnName}${(col_index<(columns?size)-1)?string(",","")}\n"
                </#list>
                + ") VALUES (\n"
                <#list columns as col>
                + ":${col.propertyName}${(col_index<(columns?size)-1)?string(",","")}\n"
                </#list>
            </#if>
                + ")";
        super.update(sql, entity);
    }
    <#if primaryKey??>
    /**
     * 通用单表更新方法
     */
    public void update(${entityCamelName} entity) {
        String sql = "UPDATE ${tableFullName!} SET\n"
            <#if columns??>
                <#assign count=1>
                <#list columns as col>
                <#if col.primaryKey == false>
                    <#assign count=count+1>
                + "${col.columnName}=:${col.propertyName}${(count<columns?size)?string(",","")}\n"
                </#if>
                </#list>
            </#if>
                + "WHERE ${primaryKey}=:${primaryProperty}";
        super.update(sql, entity);
    }
    /**
     * 通用单表删除方法
     */
    public void delete(${primaryPropertyType} ${primaryProperty}) {
        String sql = "DELETE FROM ${tableFullName!} WHERE ${primaryKey}=:${primaryProperty}";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("${primaryProperty}", ${primaryProperty});
        super.update(sql, map);
    }
    /**
     * 通用单表主键查询方法
     */
    public ${entityCamelName} fetchById(${primaryPropertyType} ${primaryProperty}) {
        String sql = "SELECT * FROM ${tableFullName!} WHERE ${primaryKey}=:${primaryProperty}";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("${primaryProperty}", ${primaryProperty});
        return super.queryForOne(sql, map);
    }
    </#if>

}
