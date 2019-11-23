package org.alibaba.filter;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;

import java.util.ArrayList;

/**
 * Created by Thinkpad on 2016/11/17.
 */
public class Slf4jLogExtFilter extends Slf4jLogFilter {
    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        this.logExecutableSql(statement, sql);
//        if(this.getS && this.isStatementLogEnabled()) {
//            statement.setLastExecuteTimeNano();
//            double nanos = (double)statement.getLastExecuteTimeNano();
//            double millis = nanos / 1000000.0D;
//            this.statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + this.stmtId(statement) + "} executed. " + millis + " millis. \n" + sql);
//        }
    }
    public void logExecutableSql(StatementProxy statement, String sql) {
        if(this.isStatementExecutableSqlLogEnable()) {
            int parametersSize = statement.getParametersSize();
            if(parametersSize == 0) {
                this.statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + this.stmtId(statement) + "} executed. \n" + sql);
            } else {
                ArrayList parameters = new ArrayList(parametersSize);

                for(int dbType = 0; dbType < parametersSize; ++dbType) {
                    JdbcParameter formattedSql = statement.getParameter(dbType);
                    //覆盖阿里的日志就是为了修改这个地方的空指针异常
                    /*
                    原因是：PreparedStatementProxyImpl这个类的setParameter这个方法，当在调用存储过程的时候如果第一个参数不是in那么就会出问题
                    {#{FLAG,mode=OUT,jdbcType=DECIMAL} = call pkg_ly.f_ly
                    (#{P_LYDD,mode=IN,jdbcType=CLOB},
                    #{P_KHDH,mode=OUT,jdbcType=VARCHAR},
                    #{P_ERROR,mode=OUT,jdbcType=VARCHAR})
		            }
		            第一个参数是OUT,那么in是第二个参数,那么这个循环的parametersSize为2，实际上入参是1，所以循环第二个的时候出现null异常
                     */
                    if(formattedSql!=null){
                        parameters.add(formattedSql.getValue());
                    }
                }

                String var7 = statement.getConnectionProxy().getDirectDataSource().getDbType();
                String var8 = SQLUtils.format(sql, var7, parameters);
                this.statementLog("{conn-" + statement.getConnectionProxy().getId() + ", " + this.stmtId(statement) + "} executed. \n" + var8);
            }
        }
    }
    private String stmtId(StatementProxy statement) {
        StringBuffer buf = new StringBuffer();
        if(statement instanceof CallableStatementProxy) {
            buf.append("cstmt-");
        } else if(statement instanceof PreparedStatementProxy) {
            buf.append("pstmt-");
        } else {
            buf.append("stmt-");
        }

        buf.append(statement.getId());
        return buf.toString();
    }
}
