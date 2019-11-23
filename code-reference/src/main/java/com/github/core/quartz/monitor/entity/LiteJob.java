package com.github.core.quartz.monitor.entity;

import org.apache.commons.lang.StringUtils;
import org.github.core.modules.mybatis.page.AbstractPageEntity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "t_job")
public class LiteJob extends AbstractPageEntity {
    private static final long serialVersionUID = 7686869501881968300L;
    /**
     * 用来存储传递数据的名称
     */
    public static String JOB_DATA_NAME = "scheduleJob";
    @Id
    @GeneratedValue(generator = "no")
    private String id;
    private String name;//不能重复，重复的话job只会创建一个
    private String detail;
    private String type;
    private String cronexpression;
    private String springid;
    private String startdelay;
    private String repeatinterval;
    private String status;
    private String czdatetime;//新增时间

    private String runxx;//执行信息；id为1的记录中的runxx表示这个系统job执行的tomcat的名字

    private String canzgs;//能够运行的总公司编号用逗号分开';
    private String notcanzgs;//'不能够运行的总公司编号';


    @Transient
    private String preTime;    //上一次执行时间
    @Transient
    private String nextTime;//下一次执行时间
    @Transient
    private String startTime;    //开始时间
    @Transient
    private String group;//job默认的组
    @Transient
    private String data;  //需要传递的数据

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public String getGroup() {
        return StringUtils.isBlank(group) ? "DEFAULT" : group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPreTime() {
        return preTime;
    }

    public void setPreTime(String preTime) {
        this.preTime = preTime;
    }

    public String getNextTime() {
        return nextTime;
    }

    public void setNextTime(String nextTime) {
        this.nextTime = nextTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = StringUtils.trim(id);
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }

    public String getName() {
        return this.name;
    }


    public void setDetail(String detail) {
        this.detail = StringUtils.trim(detail);
    }

    public String getDetail() {
        return this.detail;
    }

    public void setType(String type) {
        this.type = StringUtils.trim(type);
    }

    public String getType() {
        return this.type;
    }

    public void setCronexpression(String cronexpression) {
        this.cronexpression = StringUtils.trim(cronexpression);
    }

    public String getCronexpression() {
        return this.cronexpression;
    }

    public void setSpringid(String springid) {
        this.springid = StringUtils.trim(springid);
    }

    public String getSpringid() {
        return this.springid;
    }

    public void setStartdelay(String startdelay) {
        this.startdelay = StringUtils.trim(startdelay);
    }

    public String getStartdelay() {
        return this.startdelay;
    }

    public void setRepeatinterval(String repeatinterval) {
        this.repeatinterval = StringUtils.trim(repeatinterval);
    }

    public String getRepeatinterval() {
        return this.repeatinterval;
    }

    public String getRunxx() {
        return runxx;
    }

    public void setRunxx(String runxx) {
        this.runxx = runxx;
    }

    public String getCzdatetime() {
        return czdatetime;
    }

    public void setCzdatetime(String czdatetime) {
        this.czdatetime = czdatetime;
    }

    public String getCanzgs() {
        return canzgs;
    }

    public void setCanzgs(String canzgs) {
        this.canzgs = canzgs;
    }

    public String getNotcanzgs() {
        return notcanzgs;
    }

    public void setNotcanzgs(String notcanzgs) {
        this.notcanzgs = notcanzgs;
    }
}