## 代码自动生成工具

找到 CodeConfig 类

```
public static CodeConfig loadConfig(){
    CodeConfig cfg = new CodeConfig();
    // 路径配置
    cfg.setBaseDir("./src/main");
    cfg.setBasePackage("com.github");
    // 数据库配置
    CodeDb db = new CodeDb();
    db.setDbName("XXX");
    db.setDbType("oracle");
    db.setUser("admin");
    db.setPwd("xxxx");
    db.setDriver("oracle.jdbc.driver.OracleDriver");
    db.setUrl("jdbc:oracle:thin:@localhost:1521/xxx");
    cfg.setDb(db);

    // 生成类的包路径配置
    CodePackageSetting packageSetting = new CodePackageSetting();
    // 实体类相对 BasePackage 的包路径
    packageSetting.setEntityPackage("entity.system");
    // 实体类相对 BasePackage 的包路径
    packageSetting.setDaoPackage("dao.system");
    // 实体类相对 BasePackage 的包路径
    packageSetting.setServicePackage("service.system"); 
    cfg.setPackageSetting(packageSetting);

    List<CodeModule> modules = new ArrayList<CodeModule>();

    CodeModule module = new CodeModule();
    module.setPersistance("hibernate");
    // 模块名称
    module.setName("xxx");
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
 * @return
 */
private static List<CodeTableConf> initTableConf(){
    List<CodeTableConf> result = new ArrayList<CodeTableConf>();

    CodeTableConf m = new CodeTableConf();
    m.setName("T_TABLE123");
    m.setPrefix("T_");
    result.add(m);

    return result;
}


/**
 * 直接运行改类，生成代码
 */
public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
    DataBase2FileService reverser = new DataBase2FileService();
    /**
     * @param entity 是否生成 Entity
     * @param dao 是否生成 Dao
     * @param service 是否生成 Service
     */
    reverser.generateFiles(entity: true, dao: false, service: false);
}
```