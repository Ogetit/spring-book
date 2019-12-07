package com.github.app.util.jdbc;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.util.CollectionUtils;

import com.github.app.util.common.page.Page;

public class JdbcHelper {

    private SqlHelper sqlHelper;
    private JdbcTemplate jdbcTemplate;

    public JdbcHelper(DataSource dataSource, SqlHelper.DBType dbType) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.sqlHelper = new SqlHelper(dbType);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public SqlHelper getSqlHelper() {
        return sqlHelper;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(getJdbcTemplate());
    }

    /**
     * 查询Map对象
     */
    public Map<String, Object> queryForMap(String sql, Map<String, ?> param) {
        return this.getNamedParameterJdbcTemplate().queryForMap(sql, param);
    }

    /**
     * 查询Map对象
     */
    public Map<String, Object> queryForMap(String sql) {
        return this.getNamedParameterJdbcTemplate().queryForMap(sql, new HashMap<String, Object>());
    }

    /**
     * 查询对象
     */
    public <T> T queryForObject(String sql, Object param, Class<T> clazz) {
        T result;
        if (isPrim(clazz)) {
            result = this.getNamedParameterJdbcTemplate()
                    .queryForObject(sql, new MyBeanPropertySqlParameterSource(param), clazz);
        } else {
            result = this.getNamedParameterJdbcTemplate()
                    .queryForObject(sql, new MyBeanPropertySqlParameterSource(param),
                            BeanPropertyRowMapper.newInstance(clazz));
        }
        return result;
    }

    /**
     * 查询对象
     */
    public <T> T queryForObject(String sql, Map<String, ?> param, Class<T> clazz) {
        T result;
        if (isPrim(clazz)) {
            result =
                    this.getNamedParameterJdbcTemplate().queryForObject(sql, new MyMapSqlParameterSource(param), clazz);
        } else {
            result = this.getNamedParameterJdbcTemplate()
                    .queryForObject(sql, new MyMapSqlParameterSource(param), BeanPropertyRowMapper.newInstance(clazz));
        }
        return result;
    }

    /**
     * 查询列表
     */
    public List<Map<String, Object>> queryForListMap(String sql, Map<String, ?> param) {
        return this.getNamedParameterJdbcTemplate().queryForList(sql, param);
    }

    /**
     * 查询列表
     */
    public List<Map<String, Object>> queryForListMap(String sql) {
        return this.getNamedParameterJdbcTemplate().queryForList(sql, new HashMap<String, Object>());
    }

    /**
     * 查询一个数据
     */
    public Map<String, Object> queryForOneMap(String sql, Map<String, ?> param) {
        List<Map<String, Object>> result = queryForListMap(sql, param);
        return !CollectionUtils.isEmpty(result) ? result.get(0) : null;
    }

    /**
     * 查询一个
     */
    public Map<String, Object> queryForOneMap(String sql) {
        List<Map<String, Object>> result = queryForListMap(sql);
        return !CollectionUtils.isEmpty(result) ? result.get(0) : null;
    }

    /**
     * 查询列表
     */
    public <T> List<T> queryForList(String sql, Class<T> clazz) {
        List<T> result;
        if (isPrim(clazz)) {
            result = this.getNamedParameterJdbcTemplate().query(sql, SingleColumnRowMapper.newInstance(clazz));
        } else {
            result = this.getNamedParameterJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(clazz));
        }
        return result;
    }

    /**
     * 查询列表
     */
    public <T> List<T> queryForList(String sql, Object param, Class<T> clazz) {
        List<T> result;
        if (isPrim(clazz)) {
            result = this.getNamedParameterJdbcTemplate()
                    .queryForList(sql, new MyBeanPropertySqlParameterSource(param), clazz);
        } else {
            result = this.getNamedParameterJdbcTemplate()
                    .query(sql, new MyBeanPropertySqlParameterSource(param), BeanPropertyRowMapper.newInstance(clazz));
        }
        return result;
    }

    /**
     * 查询列表
     */
    public <T> List<T> queryForList(String sql, Map<String, ?> param, Class<T> clazz) {
        List<T> result;
        if (isPrim(clazz)) {
            result = this.getNamedParameterJdbcTemplate().queryForList(sql, new MyMapSqlParameterSource(param), clazz);
        } else {
            result = this.getNamedParameterJdbcTemplate()
                    .query(sql, new MyMapSqlParameterSource(param), BeanPropertyRowMapper.newInstance(clazz));
        }
        return result;
    }

    /**
     * 查询一个
     */
    public <T> T queryForOne(String sql, Class<T> clazz) {
        List<T> result = queryForList(sql, clazz);
        return !CollectionUtils.isEmpty(result) ? result.get(0) : null;
    }

    /**
     * 查询列表
     */
    public <T> T queryForOne(String sql, Object param, Class<T> clazz) {
        List<T> result = queryForList(sql, param, clazz);
        return !CollectionUtils.isEmpty(result) ? result.get(0) : null;
    }

    /**
     * 查询列表
     */
    public <T> T queryForOne(String sql, Map<String, ?> param, Class<T> clazz) {
        List<T> result = queryForList(sql, param, clazz);
        return !CollectionUtils.isEmpty(result) ? result.get(0) : null;
    }

    public void update(String sql) {
        this.getJdbcTemplate().update(sql);
    }

    public <T> void update(String sql, T params) {
        SqlParameterSource paramSource = new MyBeanPropertySqlParameterSource(params);
        this.getNamedParameterJdbcTemplate().update(sql, paramSource);
    }

    public void update(String sql, Map<String, ?> params) {
        this.getNamedParameterJdbcTemplate().update(sql, new MyMapSqlParameterSource(params));
    }

    public <T> void batchUpdate(String sql, List<T> list) {
        SqlParameterSource[] paramSource = this.createBatch(list.toArray());
        this.getNamedParameterJdbcTemplate().batchUpdate(sql, paramSource);
    }

    public void batchUpdate(String sql, Map<String, ?>[] list) {
        SqlParameterSource[] paramSource = this.createBatch(list);
        this.getNamedParameterJdbcTemplate().batchUpdate(sql, paramSource);
    }

    /**
     * 查询第一页 仅支持POJO
     */
    public <T> List<T> queryFirstPage(String sql, int pageSize, Object param, Class<T> clazz) {
        String pagedSql = sqlHelper.getPagedSql(sql, 1, pageSize);
        return this.queryForList(pagedSql, param, clazz);
    }

    /**
     * 查询第一页 仅支持POJO
     */
    public <T> List<T> queryFirstPage(String sql, int pageSize, Map<String, ?> param, Class<T> clazz) {
        String pagedSql = sqlHelper.getPagedSql(sql, 1, pageSize);
        return this.queryForList(pagedSql, param, clazz);

    }

    public <T> Page<T> queryPage(String sql, int currentPage, int pageSize, Object param, Class<T> clazz) {
        return this.queryPage(sql, currentPage, pageSize, new MyBeanPropertySqlParameterSource(param),
                BeanPropertyRowMapper.newInstance(clazz));
    }

    public <T> Page<T> queryPage(String sql, Page<T> page, Object param) {
        return this.queryPage(sql, page.getCurrentPage(), page.getPageSize(),
                new MyBeanPropertySqlParameterSource(param),
                BeanPropertyRowMapper.newInstance(page.getEntityClass()));
    }

    public <T> Page<T> queryPage(String sql, Page page, Object param, Class<T> clazz) {
        return this.queryPage(sql, page.getCurrentPage(), page.getPageSize(),
                new MyBeanPropertySqlParameterSource(param),
                BeanPropertyRowMapper.newInstance(clazz));
    }

    public <T> Page<T> queryPage(String sql, Page<T> page, Map<String, ?> param) {
        return this.queryPage(sql, page.getCurrentPage(), page.getPageSize(),
                new MyMapSqlParameterSource(param),
                BeanPropertyRowMapper.newInstance(page.getEntityClass()));
    }

    public <T> Page<T> queryPage(String sql, Page page, Map<String, ?> param, Class<T> clazz) {
        return this.queryPage(sql, page.getCurrentPage(), page.getPageSize(),
                new MyMapSqlParameterSource(param),
                BeanPropertyRowMapper.newInstance(clazz));
    }

    public <T> Page<T> queryPage(String sql, int currentPage, int pageSize, Map<String, ?> param, Class<T> clazz) {
        return this.queryPage(sql, currentPage, pageSize, new MyMapSqlParameterSource(param),
                BeanPropertyRowMapper.newInstance(clazz));
    }

    private <T> Page<T> queryPage(String sql, int currentPage, int pageSize, SqlParameterSource paramSource,
                                  RowMapper<T> rowMapper) {
        // 获取记录总数
        int totalCount = this.getNamedParameterJdbcTemplate()
                .queryForObject(sqlHelper.getCountSql(sql), paramSource, Integer.class);

        // 创建分页对象
        Page<T> p = new Page<T>(totalCount, pageSize, currentPage);
        // 当没有记录数时
        if (p.getTotalPage() == 0) {
            return p;
        }
        // 获取当前页数据
        List<T> data = this.getNamedParameterJdbcTemplate()
                .query(sqlHelper.getPagedSql(sql, p.getCurrentPage(), p.getPageSize()),
                        paramSource, rowMapper);
        p.setData(data);
        return p;
    }

    public <T> Page<T> queryPageMoreResult(String resultSql, String innerSql, Page page,
                                           Map<String, ?> param, Class<T> clazz) {
        return this.queryPageMoreResult(resultSql, innerSql, page.getCurrentPage(), page.getPageSize(),
                new MyMapSqlParameterSource(param), BeanPropertyRowMapper.newInstance(clazz));
    }

    public <T> Page<T> queryPageMoreResult(String resultSql, String innerSql, int currentPage, int pageSize,
                                           Map<String, ?> param, Class<T> clazz) {
        return this.queryPageMoreResult(resultSql, innerSql, currentPage, pageSize, new MyMapSqlParameterSource(param),
                BeanPropertyRowMapper.newInstance(clazz));
    }

    public <T> Page<T> queryPageMoreResult(String resultSql, String innerSql, Page page,
                                           Object param, Class<T> clazz) {
        return this.queryPageMoreResult(resultSql, innerSql, page.getCurrentPage(), page.getPageSize(),
                new MyBeanPropertySqlParameterSource(param), BeanPropertyRowMapper.newInstance(clazz));
    }

    public <T> Page<T> queryPageMoreResult(String resultSql, String innerSql, int currentPage, int pageSize,
                                           Object param, Class<T> clazz) {
        return this.queryPageMoreResult(resultSql, innerSql, currentPage, pageSize,
                new MyBeanPropertySqlParameterSource(param), BeanPropertyRowMapper.newInstance(clazz));
    }

    private <T> Page<T> queryPageMoreResult(String resultSql, String innerSql, int currentPage, int pageSize,
                                            SqlParameterSource paramSource, RowMapper<T> rowMapper) {
        // 获取记录总数
        int totalCount = this.getNamedParameterJdbcTemplate()
                .queryForObject(sqlHelper.getCountSql(innerSql), paramSource, Integer.class);

        // 创建分页对象
        Page<T> p = new Page<T>(totalCount, pageSize, currentPage);
        // 当没有记录数时
        if (p.getTotalPage() == 0) {
            return p;
        }
        // 获取当前页数据
        List<T> data = this.getNamedParameterJdbcTemplate()
                .query(sqlHelper.getPagedWithResultSql(resultSql, innerSql, p.getCurrentPage(), p.getPageSize()),
                        paramSource, rowMapper);
        p.setData(data);
        return p;
    }

    public Page<Map<String, Object>> queryPageMap(String sql, int currentPage, int pageSize) {
        return this.queryPageMap(sql, currentPage, pageSize, new HashMap<String, Object>());
    }

    public Page<Map<String, Object>> queryPageMap(String sql, Page page) {
        return this.queryPageMap(sql, page.getCurrentPage(), page.getPageSize());
    }

    public Page<Map<String, Object>> queryPageMap(String sql, Page page, Map<String, ?> param) {
        return this.queryPageMap(sql, page.getCurrentPage(), page.getPageSize(), param);
    }

    public Page<Map<String, Object>> queryPageMap(String sql, int currentPage, int pageSize, Map<String, ?> param) {
        return this.queryPageMap(sql, currentPage, pageSize, new MapSqlParameterSource(param));
    }

    private Page<Map<String, Object>> queryPageMap(String sql, int currentPage, int pageSize,
                                                   SqlParameterSource paramSource) {
        NamedParameterJdbcTemplate jdbcTemplate = this.getNamedParameterJdbcTemplate();
        // 获取记录总数
        int totalCount = jdbcTemplate.queryForObject(sqlHelper.getCountSql(sql), paramSource, Integer.class);

        // 创建分页对象
        Page<Map<String, Object>> p = new Page<Map<String, Object>>(totalCount, pageSize, currentPage);
        // 当没有记录数时
        if (p.getTotalPage() == 0) {
            return p;
        }
        // 获取当前页数据
        String pageSql = sqlHelper.getPagedSql(sql, p.getCurrentPage(), p.getPageSize());
        List<Map<String, Object>> data = jdbcTemplate.queryForList(pageSql, paramSource);
        p.setData(data);
        return p;
    }

    private <T> boolean isPrim(Class<T> clazz) {
        return clazz == String.class || clazz == Byte.class || clazz == Short.class || clazz == Integer.class
                || clazz == Long.class || clazz == Float.class || clazz == Double.class || clazz == Character.class
                || clazz == Boolean.class;
    }

    private SqlParameterSource[] createBatch(Map<String, ?>[] valueMaps) {
        MapSqlParameterSource[] batch = new MyMapSqlParameterSource[valueMaps.length];
        for (int i = 0; i < valueMaps.length; i++) {
            Map<String, ?> valueMap = valueMaps[i];
            batch[i] = new MyMapSqlParameterSource(valueMap);
        }
        return batch;
    }

    private SqlParameterSource[] createBatch(Object[] beans) {
        BeanPropertySqlParameterSource[] batch = new MyBeanPropertySqlParameterSource[beans.length];
        for (int i = 0; i < beans.length; i++) {
            Object bean = beans[i];
            batch[i] = new MyBeanPropertySqlParameterSource(bean);
        }
        return batch;
    }

    protected static class MyBeanPropertySqlParameterSource extends BeanPropertySqlParameterSource {

        public MyBeanPropertySqlParameterSource(Object object) {
            super(object);
        }

        @Override
        public int getSqlType(String var) {
            int sqlType = super.getSqlType(var);
            if (sqlType == TYPE_UNKNOWN && hasValue(var) && getValue(var) instanceof Enum) {
                sqlType = Types.VARCHAR;
            }
            return sqlType;
        }
    }

    protected static class MyMapSqlParameterSource extends MapSqlParameterSource {

        public MyMapSqlParameterSource() {
            super();
        }

        public MyMapSqlParameterSource(String paramName, Object value) {
            super(paramName, value);
        }

        public MyMapSqlParameterSource(Map<String, ?> values) {
            super(values);
        }

        @Override
        public int getSqlType(String var) {
            int sqlType = super.getSqlType(var);
            if (sqlType == TYPE_UNKNOWN && hasValue(var) && getValue(var) instanceof Enum) {
                sqlType = Types.VARCHAR;
            }
            return sqlType;
        }
    }
}

