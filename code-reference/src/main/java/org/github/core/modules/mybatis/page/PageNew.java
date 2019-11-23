package org.github.core.modules.mybatis.page;


import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 *
 * @author 章磊
 */
public class PageNew implements Serializable{
    private static final long serialVersionUID = 706816772277804610L;
    /**
     * 翻页起始行号 从0开始 如每页10条的翻页  start-endrow  0-10  10-20  20-30
     */
    private int start = 0;
    /**
     * 每页显示的条数
     */
    private int count = 30;
    /**
     * 最后的行号 start + count
     */
    private int endrow;


    public int getEndrow() {
        return start + count;
    }


    /**
     * 总记录数
     */
    private long totalCount;

    /**
     * 当前结果集合
     */
    private List list;
    /**
     * 开始执行的时间  System.currentTimeMillis()
     */
    private long begintime;

    /**
     * 执行完成的时间包含 计算总数的语句  System.currentTimeMillis()
     */
    private long endtime;

    public PageNew(int start, int count) {
        this.start = start;
        this.count = count;
    }

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


    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public long getBegintime() {
        return begintime;
    }

    public void setBegintime(long begintime) {
        this.begintime = begintime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }
}
