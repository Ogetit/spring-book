package com.github.app.dao.user;

import org.springframework.stereotype.Repository;

import com.github.app.util.jdbc.JdbcBaseDao;
import com.github.app.entity.user.User;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 应用用户表 对应 Dao 类
 */
@Repository
public class UserDao extends JdbcBaseDao<User> {
    /**
     * 通用查询方法
     */
    public List<User> select(User entity) {
        String sql = "SELECT * FROM APP_USER WHERE 1=1 ";
        if (null != entity.getId()) {
            sql += " AND id=:id";
        }
        if (null != entity.getUsername()) {
            sql += " AND username=:username";
        }
        if (null != entity.getPassword()) {
            sql += " AND password=:password";
        }
        if (null != entity.getName()) {
            sql += " AND name=:name";
        }
        if (null != entity.getPhone()) {
            sql += " AND phone=:phone";
        }
        if (null != entity.getEmail()) {
            sql += " AND email=:email";
        }
        if (null != entity.getCreateTime()) {
            sql += " AND create_time=:createTime";
        }
        if (null != entity.getUpdateTime()) {
            sql += " AND update_time=:updateTime";
        }
        if (null != entity.getUpdater()) {
            sql += " AND updater=:updater";
        }
        return super.queryForList(sql, entity);
    }
    /**
     * 通用单表插入方法
     */
    public void insert(User entity) {
        String sql = "INSERT INTO APP_USER (\n"
                + "id,\n"
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
        super.update(sql, entity);
    }
    /**
     * 通用单表更新方法
     */
    public void update(User entity) {
        String sql = "UPDATE APP_USER SET\n"
                + "username=:username,\n"
                + "password=:password,\n"
                + "name=:name,\n"
                + "phone=:phone,\n"
                + "email=:email,\n"
                + "create_time=:createTime,\n"
                + "update_time=:updateTime,\n"
                + "updater=:updater\n"
                + "WHERE id=:id";
        super.update(sql, entity);
    }
    /**
     * 通用单表删除方法
     */
    public void delete(Integer id) {
        String sql = "DELETE FROM APP_USER WHERE id=:id";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        super.update(sql, map);
    }
    /**
     * 通用单表主键查询方法
     */
    public User fetchById(Integer id) {
        String sql = "SELECT * FROM APP_USER WHERE id=:id";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        return super.queryForOne(sql, map);
    }

}
