package org.github.core.modules.mybatis.page;

import org.github.core.modules.mybatis.entity.AbstractMyBatisEntity;

import javax.persistence.Transient;

/**
 * 分页用的实体对象，如果要用到分页就必须继承这个对象
 *
 * @author 章磊
 */
public abstract class AbstractPageEntity extends AbstractMyBatisEntity {
    /**
     * 翻页起始行号 从0开始 如每页10条的翻页  start-endrow  0-10  10-20  20-30
     */
    @Transient
    private int start = 0;
    /**
     * 每页显示的条数
     */
    @Transient
    private int count = 30;
    /**
     * 最后的行号 start + count
     */
    @Transient
    private int endrow;

    @Transient
    private String orderBy;

    /**
     * 操作说明
     */
    @Transient
    private String logCzsm = "系统默认";

    /**
     * 日志来源平台
     * @see  cn.github.util.enums.ChanneEnum
     */
    @Transient
    private String logPlat = "ASMS";

    /**
     * 日志操作用户,ve_yhb 的编号，如果来源是 B2G 则去 t_member_clk.id
     */
    @Transient
    private String logUser = "System";

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getLogCzsm() {
        return logCzsm;
    }

    public void setLogCzsm(String logCzsm) {
        this.logCzsm = logCzsm;
    }

    public String getLogPlat() {
        return logPlat;
    }

    public void setLogPlat(String logPlat) {
        this.logPlat = logPlat;
    }

    public String getLogUser() {
        return logUser;
    }

    public void setLogUser(String logUser) {
        this.logUser = logUser;
    }

    public int getEndrow() {
        return start+count;
    }

}
