package ${basePackage}.${moduleName}.${servicePackage};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${basePackage}.${moduleName}.${daoPackage}.${entityCamelName}Dao;
<#--import ${basePackage}.${moduleName}.${entityPackage}.${entityCamelName};-->

/**
 * ${remark!} 对应 Dao 类
 */
<#assign type=primaryPropertyType>
<#assign type=type?replace("java.util.","")>
<#assign type=type?replace("java.math.","")>
@Service
public class ${entityCamelName}Service {
    @Autowired
    private ${entityCamelName}Dao ${entityName}Dao;
}
