package com.github.app;

import org.junit.Test;

import com.github.app.util.jdbc.SqlHelper;

/**
 * 文件描述 测试工具类
 *
 * @author ouyangjie
 * @Title: SqlHelperTest
 * @ProjectName spring-book
 * @date 2019/12/2 11:13 PM
 */
public class SqlHelperTest {
    @Test
    public void test() {
        SqlHelper helper = new SqlHelper(SqlHelper.DBType.MYSQL);
        String sql = "select * from (select a, b from x where a>0 and b>0 order by a, \t b)     n \nwhere a>0";
        System.out.println(helper.getCountSql(sql, null));
        sql = "select * from (select a, b from x where a>0 and b>0 order by a, \t b)     n ";
        System.out.println(helper.getCountSql(sql, null));
        sql = "select a, b from x where a>0 and b>0 order by a, \t b ";
        System.out.println(helper.getCountSql(sql, null));
        sql = "select * from (select a, b from x where a>0 and b>0 order by field(name,a,?,?,?,?,\t b))     n ";
        System.out.println(helper.getCountSql(sql, null));
    }
}
