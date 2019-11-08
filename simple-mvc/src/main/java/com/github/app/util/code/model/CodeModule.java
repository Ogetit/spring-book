package com.github.app.util.code.model;

import java.util.List;

/**
 * 模块信息
 */
public class CodeModule {
    private String name; // 模块名称
    /**
     * 启用的持久层框架，选项有：hibernate,mybatis,jdbc，其中hibernate为默认
     */
    private String persistance;
    // 是否删除表前缀
    private boolean isDeleteTablePrefix;
    // 默认保存目录，文件保存到该目录下，暂时不使用此属性值
    private String savePath;
    private String framework;
    // 使用前端框架，可设置为dorado和mvc，mvc表示使用spring-mvc
    private String daoPackage;
    private String daoImplPackage;
    private String servicePackage;
    private String serviceImplPackage;
    private String entityPackage;
    private String actionPackage;
    private String viewPackage;
    private String mapperPackage;
    private String myBatisPackage;
    private CodePackageSetting defaultPackageSetting;
    // 配置的数据表信息
    private List<CodeTableConf> tables;
    // 模块使用的页面模板，可独立于全局配置的模板，如果不设置，则默认使用全局配置
    private String theme;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleteTablePrefix() {
        return isDeleteTablePrefix;
    }

    public void setDeleteTablePrefix(boolean isDeleteTablePrefix) {
        this.isDeleteTablePrefix = isDeleteTablePrefix;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getDaoPackage() {
        if (null == daoPackage && null != defaultPackageSetting) {
            return defaultPackageSetting.getDaoPackage();
        }
        return daoPackage;
    }

    public void setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
    }

    public String getDaoImplPackage() {
        if (null == daoImplPackage && null != defaultPackageSetting) {
            return defaultPackageSetting.getDaoImplPackage();
        }
        return daoImplPackage;
    }

    public void setDaoImplPackage(String daoImplPackage) {
        this.daoImplPackage = daoImplPackage;
    }

    public String getServicePackage() {
        if (null == servicePackage && null != defaultPackageSetting) {
            return defaultPackageSetting.getServicePackage();
        }
        return servicePackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public String getServiceImplPackage() {
        if (null == serviceImplPackage && null != defaultPackageSetting) {
            return defaultPackageSetting.getServiceImplPackage();
        }
        return serviceImplPackage;
    }

    public void setServiceImplPackage(String serviceImplPackage) {
        this.serviceImplPackage = serviceImplPackage;
    }

    public String getEntityPackage() {
        if (null == entityPackage && null != defaultPackageSetting) {
            return defaultPackageSetting.getEntityPackage();
        }
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public String getActionPackage() {
        if (null == actionPackage && null != defaultPackageSetting) {
            return defaultPackageSetting.getActionPackage();
        }
        return actionPackage;
    }

    public void setActionPackage(String actionPackage) {
        this.actionPackage = actionPackage;
    }

    public String getViewPackage() {
        if (null == viewPackage && null != defaultPackageSetting) {
            return defaultPackageSetting.getViewPackage();
        }
        return viewPackage;
    }

    public void setViewPackage(String viewPackage) {
        this.viewPackage = viewPackage;
    }

    public String getMapperPackage() {
        if (null == mapperPackage && null != defaultPackageSetting) {
            return defaultPackageSetting.getMapperPackage();
        }
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public List<CodeTableConf> getTables() {
        return tables;
    }

    public void setTables(List<CodeTableConf> tables) {
        this.tables = tables;
    }

    public String getPersistance() {
        return persistance;
    }

    public void setPersistance(String persistance) {
        this.persistance = persistance;
    }

    public String getMyBatisPackage() {
        return myBatisPackage;
    }

    public void setMyBatisPackage(String mybatisPackage) {
        this.myBatisPackage = mybatisPackage;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public CodePackageSetting getDefaultPackageSetting() {
        return defaultPackageSetting;
    }

    public void setDefaultPackageSetting(CodePackageSetting defaultPackageSetting) {
        this.defaultPackageSetting = defaultPackageSetting;
    }
}
