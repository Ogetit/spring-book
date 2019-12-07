package com.github.app.util.jdbc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Strings;

/**
 * SQL 生成工具
 */
public class SqlHelper {

    private static final Logger logger = LoggerFactory.getLogger(SqlHelper.class);

    public enum DBType {
        ORACLE, MYSQL, DB2, TERA
    }

    private DBType dbType;

    public SqlHelper(DBType dbType) {
        this.dbType = dbType;
    }

    public DBType getDbType() {
        if (this.dbType == null) {
            throw new IllegalArgumentException("dbType of SqlHelper must not be null.");
        }
        return this.dbType;
    }

    public String getPagedSql(String innerSql, long currPage, long pageSize) {
        long begin = (currPage - 1) * pageSize;
        long end = begin + pageSize;
        StringBuilder strRet = new StringBuilder("");
        switch (this.getDbType()) {
            case MYSQL:
                strRet.append(innerSql).append(" limit ").append(begin).append(",").append(pageSize);
                break;
            case ORACLE:
                String rownumStr = "ROENUM" + System.currentTimeMillis();
                strRet.append("SELECT * FROM")
                        .append("SELECT ROWNUM AS ").append(rownumStr).append(", d.* from (")
                        .append(innerSql)
                        .append(") d WHERE ROWNUM < ").append(end)
                        .append(") WHERE ").append(rownumStr).append(" > ").append(begin);
                break;
            case DB2:
                StringBuilder rowNumber = new StringBuilder(" rownumber() over(");
                int orderByIndex = innerSql.toLowerCase().indexOf("order by");
                if (orderByIndex > 0) {
                    String[] tempStr = innerSql.substring(orderByIndex).split("\\.");
                    for (int i = 0; i < tempStr.length - 1; i++) {
                        int dotIndex = tempStr[i].lastIndexOf(",");
                        if (dotIndex < 0) {
                            dotIndex = tempStr[i].lastIndexOf(" ");
                        }
                        String result = tempStr[i].substring(0, dotIndex + 1);
                        rowNumber.append(result).append(" temp_.");
                    }
                    rowNumber.append(tempStr[tempStr.length - 1]);
                }
                rowNumber.append(") as row_,");
                strRet.append(innerSql.length() + 100).append("select * from ( ")
                        .append(" select ").append(rowNumber).append("temp_.* from (")
                        .append(innerSql).append(" ) as temp_").append(" ) as temp2_")
                        .append(" where row_  between ").append(begin).append("+1 and ").append(end);
                break;
            case TERA:
                strRet = new StringBuilder(innerSql.length() + 100);
                strRet.append(innerSql);
                int orderByIndexTera = strRet.toString().toLowerCase().lastIndexOf("order by");
                if (orderByIndexTera > 0) {
                    String orderBy = strRet.substring(orderByIndexTera);
                    strRet.insert(orderByIndexTera, " QUALIFY row_number() OVER( ");
                    strRet.append(" ) ");
                    strRet.append(" between ").append(begin).append(" and ").append(end);
                    strRet.append(orderBy);
                } else {
                    strRet.append(" QUALIFY sum(1) over (rows unbounded preceding) between ")
                            .append(begin).append(" and ").append(end);
                }
                break;
            default:
                break;
        }
        String result = strRet.toString();
        logger.debug(result);
        return result;
    }

    // 重定义复杂返回集，用于简化查询，优化速度，对分页后的结果做二次处理用，结果集引用原Sql里面的值用前缀 t
    public String getPagedWithResultSql(String resultSql, String innerSql, int currPage, int pageSize) {
        if (resultSql == null) {
            resultSql = "t.*";
        }
        int begin = (currPage - 1) * pageSize;
        int end = begin + pageSize;
        String strRet;
        switch (this.getDbType()) {
            case MYSQL:
                strRet = "select " + resultSql + " from (" + (innerSql + " limit " + begin + "," + pageSize) + ") t";
                break;
            case ORACLE:
                // 如果end是Integer的最大值，再自加1变成Integer的最小值，因此，这里加判断
                if (end < Integer.MAX_VALUE) {
                    end++;
                }
                strRet = "SELECT " + resultSql
                        + " FROM (SELECT ROWNUM AS NUMROW, d.* from (" + innerSql + ") d WHERE ROWNUM" + " < " + end
                        + " ) t WHERE NUMROW > " + begin;
                break;
            case DB2:
                StringBuilder rowNumber = new StringBuilder(" rownumber() over(");
                int orderByIndex = innerSql.toLowerCase().indexOf("order by");

                if (orderByIndex > 0) {
                    String[] tempStr = innerSql.substring(orderByIndex).split("\\.");
                    for (int i = 0; i < tempStr.length - 1; i++) {
                        int dotIndex = tempStr[i].lastIndexOf(",");
                        if (dotIndex < 0) {
                            dotIndex = tempStr[i].lastIndexOf(" ");
                        }
                        String result = tempStr[i].substring(0, dotIndex + 1);
                        rowNumber.append(result).append(" temp_.");
                    }
                    rowNumber.append(tempStr[tempStr.length - 1]);
                }
                rowNumber.append(") as row_,");

                StringBuffer pagingSelect = new StringBuffer(innerSql.length() + 100)
                        .append("select ").append(resultSql).append(" from ( ")
                        .append(" select ").append(rowNumber).append("temp_.* from (")
                        .append(innerSql).append(" ) as temp_").append(" ) as t")
                        .append(" where row_  between ").append(begin).append("+1 and ").append(end);
                strRet = new String(pagingSelect);
                break;
            case TERA:
                StringBuilder builder = new StringBuilder(innerSql.length() + 100);
                builder.append("select " + resultSql + " from (" + innerSql + "） t");
                int orderByIndexTera = builder.toString().toLowerCase().lastIndexOf("order by");
                if (orderByIndexTera > 0) {
                    String orderBy = builder.substring(orderByIndexTera);
                    builder.insert(orderByIndexTera, " QUALIFY row_number() OVER( ");
                    builder.append(" ) ");
                    builder.append(" between ").append(begin).append(" and ").append(end);
                    builder.append(orderBy);
                } else {
                    builder.append(" QUALIFY sum(1) over (rows unbounded preceding) between ")
                            .append(begin).append(" and ").append(end);
                }
                strRet = builder.toString();
                break;
            default:
                strRet = "select " + resultSql + " from (" + innerSql + "） t";
                break;
        }
        logger.debug(strRet);
        return strRet;
    }

    public String getCountSql(String sql) {
        return getCountSql(sql, null);
    }

    public String getCountSql(String originalSql, String countColumn) {
        if (null == countColumn || countColumn.isEmpty()) {
            countColumn = "1";
        }
        // do count
        boolean sliceOrderBy = false;
        final String lowerSql = originalSql.toLowerCase().trim();
        final int orderIndex = originalSql.toLowerCase().lastIndexOf("order");
        if (orderIndex != -1) {
            String remainSql = lowerSql.substring(orderIndex + "order".length()).trim();
            sliceOrderBy = remainSql.startsWith("by");
            if (sliceOrderBy) {
                remainSql = StringUtils.replace(remainSql, "("," ( ");
                remainSql = StringUtils.replace(remainSql,")"," ) ");
                // 用 空格 或 , 分隔 order 后面 的 字符串
                String[] remainSqlWordArr = remainSql.split("[\\s,]+");
                for (String s : remainSqlWordArr) {
                    List<String> keywordLsit = Arrays.asList("select","?", "union", "from", "where", "and", "or",
                            "between", "in", "case");
                    // 后面的字符串是否包含特殊关键字
                    if (null != s && !s.isEmpty() && keywordLsit.contains(s)) {
                        sliceOrderBy = false;
                        break;
                    }
                }
                // 后面的字符串是否有非成对的 括号
                if (sliceOrderBy) {
                    int leftBracketsCount = 0;
                    for (String s : remainSqlWordArr) {
                        if (s == null || s.isEmpty()) {
                            continue;
                        } else if (s.equals("(")) {
                            leftBracketsCount++;
                        } else if (s.equals(")")) {
                            leftBracketsCount--;
                            if (leftBracketsCount < 0) {
                                sliceOrderBy = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (sliceOrderBy) {
            originalSql = originalSql.trim().substring(0, orderIndex).trim();
        }
        String countSql =  "select count(" + countColumn + ") as count from (" + originalSql + ") tmp_count";
        logger.debug(countSql);
        return countSql;
    }

}
