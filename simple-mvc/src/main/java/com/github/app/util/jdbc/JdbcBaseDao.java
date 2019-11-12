package com.github.app.util.jdbc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.app.util.common.page.Page;
import com.github.app.util.refect.ReflectionUtil;

/**
 * 文件描述 jdbc 基础 Dao
 *
 * @author ouyangjie
 * @Title: JdbcBaseDao
 * @ProjectName spring-book
 * @date 2019/11/12 4:20 PM
 */
public abstract class JdbcBaseDao<T extends Serializable> {
    @Autowired
    protected JdbcHelper jdbcHelper;

    protected Class<T> entityClass;

    public JdbcBaseDao() {
        this.entityClass = ReflectionUtil.getSuperClassGenricType(getClass());
    }

    /**
     * 查询Map对象
     */
    public Map<String, Object> queryForMap(String sql, Map<String, ?> param) {
        return this.jdbcHelper.queryForMap(sql, param);
    }

    /**
     * 查询Map对象
     */
    public Map<String, Object> queryForMap(String sql) {
        return this.jdbcHelper.queryForMap(sql);
    }

    /**
     * 查询列表
     */
    public List<Map<String, Object>> queryForListMap(String sql, Map<String, ?> param) {
        return this.jdbcHelper.queryForListMap(sql, param);
    }

    /**
     * 查询列表
     */
    public List<Map<String, Object>> queryForListMap(String sql) {
        return this.jdbcHelper.queryForListMap(sql);
    }

    /**
     * 查询一个数据
     */
    public Map<String, Object> queryForOneMap(String sql, Map<String, ?> param) {
        return this.jdbcHelper.queryForOneMap(sql, param);
    }

    /**
     * 查询一个
     */
    public Map<String, Object> queryForOneMap(String sql) {
        return this.jdbcHelper.queryForOneMap(sql);
    }

    /**
     * 查询列表
     */
    public List<T> queryForList(String sql) {
        return this.jdbcHelper.queryForList(sql, this.entityClass);
    }

    /**
     * 查询列表
     */
    public List<T> queryForList(String sql, Object param) {
        return this.jdbcHelper.queryForList(sql, param, this.entityClass);
    }

    /**
     * 查询列表
     */
    public List<T> queryForList(String sql, Map<String, ?> param) {
        return this.jdbcHelper.queryForList(sql, param, this.entityClass);
    }

    /**
     * 查询一个
     */
    public T queryForOne(String sql) {
        return this.jdbcHelper.queryForOne(sql, this.entityClass);
    }

    /**
     * 查询列表
     */
    public T queryForOne(String sql, Object param) {
        return this.jdbcHelper.queryForOne(sql, param, this.entityClass);
    }

    /**
     * 查询列表
     */
    public T queryForOne(String sql, Map<String, ?> param) {
        return this.jdbcHelper.queryForOne(sql, param, this.entityClass);
    }

    public void update(String sql) {
        this.jdbcHelper.update(sql);
    }

    public void update(String sql, T params) {
        this.jdbcHelper.update(sql, params);
    }

    public void update(String sql, Map<String, ?> params) {
        this.jdbcHelper.update(sql, params);
    }

    public void batchUpdate(String sql, List<T> list) {
        this.jdbcHelper.batchUpdate(sql, list);
    }

    public void batchUpdate(String sql, Map<String, ?>[] list) {
        this.jdbcHelper.batchUpdate(sql, list);
    }

    /**
     * 查询第一页 仅支持POJO
     */
    public List<T> queryFirstPage(String sql, int pageSize, Object param) {
        return this.jdbcHelper.queryFirstPage(sql, pageSize, param, this.entityClass);
    }

    /**
     * 查询第一页 仅支持POJO
     */
    public List<T> queryFirstPage(String sql, int pageSize, Map<String, ?> param) {
        return this.jdbcHelper.queryFirstPage(sql, pageSize, param, this.entityClass);

    }

    public Page<T> queryPage(String sql, int currentPage, int pageSize, Object param) {
        return this.jdbcHelper.queryPage(sql, currentPage, pageSize, param, this.entityClass);
    }

    public Page<T> queryPage(String sql, Page page, Object param) {
        return this.jdbcHelper.queryPage(sql, page, param, this.entityClass);
    }

    public Page<T> queryPage(String sql, Page page, Map<String, ?> param) {
        return this.jdbcHelper.queryPage(sql, page, param, this.entityClass);
    }

    public Page<T> queryPage(String sql, int currentPage, int pageSize, Map<String, ?> param) {
        return this.jdbcHelper.queryPage(sql, currentPage, pageSize, param, this.entityClass);
    }

    public Page<Map<String, Object>> queryPageMap(String sql, int currentPage, int pageSize) {
        return this.jdbcHelper.queryPageMap(sql, currentPage, pageSize, new HashMap<String, Object>());
    }

    public Page<Map<String, Object>> queryPageMap(String sql, Page page) {
        return this.jdbcHelper.queryPageMap(sql, page);
    }

    public Page<Map<String, Object>> queryPageMap(String sql, Page page, Map<String, ?> param) {
        return this.jdbcHelper.queryPageMap(sql, page, param);
    }

    public Page<Map<String, Object>> queryPageMap(String sql, int currentPage, int pageSize, Map<String, ?> param) {
        return this.jdbcHelper.queryPageMap(sql, currentPage, pageSize, param);
    }
}
