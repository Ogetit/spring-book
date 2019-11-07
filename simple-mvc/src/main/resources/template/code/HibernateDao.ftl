package ${basePackage}.${moduleName}.${daoPackage};

import org.springframework.stereotype.Repository;

import ${basePackage}.${moduleName}.${entityPackage}.${entityCamelName};
<#if importClassList??>
	<#list importClassList as imp>
    import ${imp!};
	</#list>
</#if>

/**
 * ${remark!} 对应 Dao 类
 */
<#assign type=primaryPropertyType>
<#assign type=type?replace("java.util.","")>
<#assign type=type?replace("java.math.","")>
@Repository
public class ${entityCamelName}Dao extends HibernateDao<${entityCamelName}, ${type}> {
}
