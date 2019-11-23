package org.alibaba.filter;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.filter.stat.StatFilterContext;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.sql.SQLException;

/**
 * Created by java on 2017/6/26.
 * 检查查询数量大于200的sql
 */
public class DruidStatFilter extends StatFilter {
    private final static Log LOG = LogFactory.getLog(DruidStatFilter.class);
    protected long fetchRowNum = 200;

    public long getFetchRowNum() {
        return fetchRowNum;
    }

    public void setFetchRowNum(long fetchRowNum) {
        this.fetchRowNum = fetchRowNum;
    }


    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {

        long nanos = System.nanoTime() - resultSet.getConstructNano();

        int fetchRowCount = resultSet.getFetchRowCount();

        JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
        dataSourceStat.getResultSetStat().afterClose(nanos);
        dataSourceStat.getResultSetStat().addFetchRowCount(fetchRowCount);
        dataSourceStat.getResultSetStat().incrementCloseCounter();

        StatFilterContext.getInstance().addFetchRowCount(fetchRowCount);

        String sql = resultSet.getSql();
        if (sql != null) {
            JdbcSqlStat sqlStat = resultSet.getSqlStat();
            if (sqlStat != null && resultSet.getCloseCount() == 0) {
                sqlStat.addFetchRowCount(fetchRowCount);
                long stmtExecuteNano = resultSet.getStatementProxy().getLastExecuteTimeNano();
                sqlStat.addResultSetHoldTimeNano(stmtExecuteNano, nanos);
                if (resultSet.getReadStringLength() > 0) {
                    sqlStat.addStringReadLength(resultSet.getReadStringLength());
                }
                if (resultSet.getReadBytesLength() > 0) {
                    sqlStat.addReadBytesLength(resultSet.getReadBytesLength());
                }
                if (resultSet.getOpenInputStreamCount() > 0) {
                    sqlStat.addInputStreamOpenCount(resultSet.getOpenInputStreamCount());
                }
                if (resultSet.getOpenReaderCount() > 0) {
                    sqlStat.addReaderOpenCount(resultSet.getOpenReaderCount());
                }
            }
        }
        if (fetchRowCount >= fetchRowNum) {
            if (logSlowSql) {
                LOG.error("fetchRowCount sql " + fetchRowCount + " count.>="+fetchRowNum+" \n" + sql);
            }
        }

        chain.resultSet_close(resultSet);

        StatFilterContext.getInstance().resultSet_close(nanos);
    }
}
