package ${basePackage}.${moduleName}.${entityPackage};

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
<#if importClassList??>
<#list importClassList as imp>
import ${imp!};
</#list>
</#if>

/**
 * ${remark!} 对应实体类
 */
@Entity
@Table(name = "${tableFullName!}")
public class ${entityCamelName!} implements Serializable {

    private static final long serialVersionUID = 1L;

    <#if columns??>
    <#list columns as col>
    /**
     * ${col.remark!}
     */
    <#assign type=col.propertyType>
    <#assign type=type?replace("java.util.","")>
    <#assign type=type?replace("java.math.","")>
    <#assign defaultValue=col.defaultValue!>
    <#if defaultValue?length gt 0>
        <#if type=="Date">
            <#assign defaultValue="new Date()">
        <#elseif type=="Long">
            <#assign defaultValue=defaultValue+"L">
        <#elseif type=="Double">
            <#assign defaultValue=defaultValue+"d">
        <#elseif type=="BigDecimal">
            <#assign defaultValue="BigDecimal.valueOf("+defaultValue+")">
        <#elseif type=="String">
            <#assign defaultValue="\""+defaultValue+"\"">
        </#if>
	</#if>
    <#if col.nullable==false>
        <#if type=="String">
    @NotEmpty(message = "${col.remark!}不能为空")
        <#else>
    @NotNull(message = "${col.remark!}不能为空")
        </#if>
    </#if>
    <#if col.primaryKey>
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "${col.columnName}", unique = true, nullable = false, precision = 22)
    <#else>
    @Column(name = "${col.columnName}", columnDefinition = "${col.columnType}")
    </#if>
    private ${type!} ${col.propertyName}${(defaultValue?length>0)?string(" = "+defaultValue,"")};
    </#list>
    </#if>

    <#list columns as col>
    <#assign type=col.propertyType>
    <#assign type=type?replace("java.util.","")>
    <#assign type=type?replace("java.math.","")>

    public ${type} get${col.propertyCamelName}() {
        return this.${col.propertyName};
    }

    public void set${col.propertyCamelName}(${type} ${col.propertyName}) {
        this.${col.propertyName} = ${col.propertyName};
    }
    </#list>
}