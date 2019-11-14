package ${basePackage}.${moduleName}.${servicePackage};

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${basePackage}.${moduleName}.${daoPackage}.${entityCamelName}Dao;
import ${basePackage}.${moduleName}.${entityPackage}.${entityCamelName};

/**
 * ${remark!} 对应 Service 类
 */
@Service
public class ${entityCamelName}Service {
    @Autowired
    private ${entityCamelName}Dao ${entityName}Dao;

    /**
     * 通用查询方法
     */
    public List<${entityCamelName}> fetch(${entityCamelName} entity) {
        return ${entityName}Dao.select(entity);
    }

    /**
     * 通用单表插入方法
     */
    public void insert(${entityCamelName} entity) {
        ${entityName}Dao.insert(entity);
    }

    <#if primaryKey??>
    /**
     * 通用单表更新方法
     */
    public void update(${entityCamelName} entity) {
        ${entityName}Dao.update(entity);
    }

    /**
     * 通用单表删除方法
     */
    public void delete(${primaryPropertyType} ${primaryProperty}) {
        ${entityName}Dao.delete(${primaryProperty});
    }

    /**
     * 通用单表主键查询方法
     */
    public ${entityCamelName} fetchById(${primaryPropertyType} ${primaryProperty}) {
        return ${entityName}Dao.fetchById(${primaryProperty});
    }
    </#if>
}