package ${basePackage}.${moduleName}.${entityPackage};

import java.io.Serializable;
<#if importClassList??>
<#list importClassList as imp>
import ${imp!};
</#list>
</#if>
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * ${remark!} 对应实体类
 * @author ouyangjie
 * @Title: ${entityCamelName!}
 * @ProjectName ifs-marketing
 */
@ApiModel(value = "${remark!}")
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
    @ApiModelProperty(value = "${col.remark!}")
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