package com.github.app.util.code;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.app.util.code.model.CodeDb;
import com.github.app.util.code.model.CodeModule;
import com.github.app.util.code.model.CodePackageSetting;
import com.github.app.util.code.model.CodeTableConf;
import com.github.app.util.code.service.DataBase2FileService;

/**
 * 读取配置文件
 */
public class CodeConfig {
    // 文件存放默认路径
    private String baseDir;
    // 包路径的前缀，如com.mars，后面则跟上模块名等
    private String basePackage;
    // 连接数据库的配置信息
    private CodeDb db;
    // 要生成的代码模块列表
    private List<CodeModule> modules;
    // 全局包名设置
    private CodePackageSetting packageSetting;
    // 全局界面模板风格
    private String theme;
    // 页面类型
    private String pageType;
    private List<String> ignoreColumns = new ArrayList<String>();

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public CodeDb getDb() {
        return db;
    }

    public void setDb(CodeDb db) {
        this.db = db;
    }

    public List<CodeModule> getModules() {
        return modules;
    }

    public void setModules(List<CodeModule> modules) {
        this.modules = modules;
    }

    public CodePackageSetting getPackageSetting() {
        return packageSetting;
    }

    public void setPackageSetting(CodePackageSetting packageSetting) {
        this.packageSetting = packageSetting;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public List<String> getIgnoreColumns() {
        return ignoreColumns;
    }

    public void setIgnoreColumns(List<String> ignoreColumns) {
        this.ignoreColumns = ignoreColumns;
    }

    public static CodeConfig loadConfig() {
        CodeConfig cfg = new CodeConfig();
        // 路径配置
        cfg.setBaseDir("/Users/ouyangjie/WorkSpace/Java/spring-book/simple-mvc/src/main");
        cfg.setBasePackage("com.github");
        // 数据库配置
        CodeDb db = new CodeDb();
        db.setDbName("spring_book");
        db.setDbType("mysql");
        db.setUser("root");
        db.setPwd("123456");
        db.setDriver("com.mysql.jdbc.Driver");
        db.setUrl("jdbc:mysql://localhost:8306/spring_book");
        cfg.setDb(db);

        // 生成类的包路径配置
        CodePackageSetting packageSetting = new CodePackageSetting();
        // 实体类相对 BasePackage 的包路径
        packageSetting.setEntityPackage("entity.user");
        // 实体类相对 BasePackage 的包路径
        packageSetting.setDaoPackage("dao.user");
        // 实体类相对 BasePackage 的包路径
        packageSetting.setServicePackage("service.user");
        cfg.setPackageSetting(packageSetting);

        List<CodeModule> modules = new ArrayList<CodeModule>();

        CodeModule module = new CodeModule();
        module.setPersistance("jdbc");
        // 模块名称
        module.setName("app");
        module.setDeleteTablePrefix(true);
        module.setFramework("mvc");
        // 表信息配置，如果不配，默认生成  'like upper(module.name) + %' 的表，并且类名不去表名前缀
        module.setTables(initTableConf());
        module.setDefaultPackageSetting(packageSetting);
        modules.add(module);

        cfg.setModules(modules);

        return cfg;
    }

    /**
     * 初始化配置的表格信息
     *
     * @return
     */
    private static List<CodeTableConf> initTableConf() {
        List<CodeTableConf> result = new ArrayList<CodeTableConf>();

        CodeTableConf m = new CodeTableConf();
        m.setName("APP_USER");
        m.setPrefix("APP_");
        result.add(m);

        return result;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        DataBase2FileService reverser = new DataBase2FileService();
        // 只生成 Entity
        reverser.generateFiles(true, true, false);
    }
}
