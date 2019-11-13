package com.github.app.dao.user;

import org.springframework.stereotype.Repository;

import com.github.app.util.jdbc.JdbcBaseDao;
import com.github.app.entity.user.AppUser;
    import java.util.Date;

/**
 * 应用用户表 对应 Dao 类
 */
@Repository
public class AppUserDao extends JdbcBaseDao<AppUser> {
    /**
     * 通用单表插入方法
     */
    public void insert(AppUser entity) {
        String sql = "INSERT INTO APP_USER (\n"
                + "ID,\n"
                + "username,\n"
                + "password,\n"
                + "name,\n"
                + "phone,\n"
                + "email,\n"
                + "create_time,\n"
                + "update_time,\n"
                + "updater\n"
                + ") VALUES (\n"
                + ":id,\n"
                + ":username,\n"
                + ":password,\n"
                + ":name,\n"
                + ":phone,\n"
                + ":email,\n"
                + ":createTime,\n"
                + ":updateTime,\n"
                + ":updater\n"
                + ")";
        this.update(sql, entity);
    }


}
