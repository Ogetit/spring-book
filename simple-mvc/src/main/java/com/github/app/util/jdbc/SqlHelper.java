/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.github.app.util.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public String getCountSql(String sql) {
        String countSql = "select count(*) as count from (" + sql + ") count";
        logger.debug(countSql);
        return countSql;
    }

}
