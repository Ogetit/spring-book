package com.github.core.quartz.monitor.service;

import com.github.core.quartz.monitor.entity.LiteJob;
import com.github.core.quartz.monitor.ILiteJob;
import com.github.core.quartz.monitor.dao.LiteJobDao;

import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.github.core.modules.exception.BusinessException;
import org.github.core.modules.exception.SystemErrorCode;
import org.github.core.modules.service.BaseService;
import org.github.core.modules.service.SpringContextUtil;
import org.github.core.modules.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuartzMonitor extends BaseService<LiteJob, LiteJobDao> {
    @Autowired
    private SchedulerFactoryBean localQuartzScheduler;
    @Autowired
    private LiteJobDao liteJobDao;
    @Autowired
    private LiteQuartzService liteQuartzService;

    private ILiteJob firstDataJob;

    /**
     * 运行JOB的tomcat名字， 1表示运行  其他表示不运行
     */
    public static String CANRUNJOB;

    /**
     * 获取所有计划中的任务列表
     *
     * @return
     *
     * @throws SchedulerException
     */
    public List<LiteJob> queryAll() throws BusinessException {
        try {
            Scheduler scheduler = localQuartzScheduler.getScheduler();
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            LiteJob liteJob = new LiteJob();
            liteJob.setStatus("0");
            List<LiteJob> jobList = liteJobDao.select(liteJob);
            for (JobKey jobKey : jobKeys) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    LiteJob job = new LiteJob();
                    job.setName(jobKey.getName());
                    job.setGroup(jobKey.getGroup());
                    job.setDetail(trigger.getDescription());
                    LiteJob oldJob = (LiteJob) jobDetail.getJobDataMap().get(LiteJob.JOB_DATA_NAME);
                    if (oldJob != null) {
                        job.setId(oldJob.getId());
                        job.setSpringid(oldJob.getSpringid());
                        job.setType(oldJob.getType());
                    }
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    job.setStatus(triggerState.name());

                    job.setPreTime(DateUtil.dateToStrLong(trigger.getPreviousFireTime()));
                    job.setNextTime(DateUtil.dateToStrLong(trigger.getNextFireTime()));
                    job.setStartTime(DateUtil.dateToStrLong(trigger.getStartTime()));
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        job.setCronexpression(cronExpression);
                        job.setType("1");
                    } else if (trigger instanceof SimpleTrigger) {
                        job.setRepeatinterval(((SimpleTrigger) trigger).getRepeatInterval() + "");
                        job.setType("2");
                    }
                    jobList.add(job);
                }
            }
            return jobList;
        } catch (Exception e) {
            logger.error("查询所有任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "查询失败:" + e.getMessage());
        }
    }

    /**
     * 获得运行时的任务
     * 运行时是进入我们写的任务的方法中才算运行时,执行完了就不算了
     *
     * @return
     *
     * @throws Exception
     */
    public List<LiteJob> queryRunAll() throws Exception {
        Scheduler scheduler = localQuartzScheduler.getScheduler();
        List<JobExecutionContext> executingJobs = scheduler
                .getCurrentlyExecutingJobs();
        List<LiteJob> jobList = new ArrayList<LiteJob>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            LiteJob job = new LiteJob();
            JobDetail jobDetail = executingJob.getJobDetail();
            JobKey jobKey = jobDetail.getKey();
            Trigger trigger = executingJob.getTrigger();
            job.setName(jobKey.getName());
            job.setDetail(trigger.getDescription());
            Trigger.TriggerState triggerState = scheduler
                    .getTriggerState(trigger.getKey());
            job.setStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronexpression(cronExpression);
            }
            jobList.add(job);
        }
        return jobList;
    }

    public void startJob() {
        //只有指定机器才能运行
        try {
            if (firstDataJob != null) {
                firstDataJob.execute();
            }
            List<LiteJob> jobs = this.queryList(new LiteJob());
            if (!canRunJob(jobs)) {
                CANRUNJOB = "0";
                return;
            }
            for (LiteJob job : jobs) {
                if ("0".equals(job.getStatus()) || "1".equals(job.getId())) {
                    logger.info("任务:" + job.getName() + ":" + job.getDetail() + "没有开启");
                    continue;
                }
                logger.info("启动任务:" + job.getName() + ":" + job.getDetail());
                String springid = job.getSpringid();
                if (!SpringContextUtil.containsBean(springid)) {
                    logger.info("启动任务失败:" + job.getName() + ":" + job.getDetail() + "服务名没有找到");
                    continue;
                }
                // 添加定时执行策略的job
                liteQuartzService.doWhat(job, LiteQuartzService.DOJOB.ADD);
            }
            CANRUNJOB = "1";
        } catch (Exception e) {
            logger.error("启动任务失败", e);
            // throw new BusinessException(SystemErrorCode.SystemErrorCode,e,"启动任务失败:"+ e.getMessage());
        }
    }

    private boolean canRunJob(List<LiteJob> jobs) throws BusinessException {
        String tomname = "";
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        if (applicationContext instanceof XmlWebApplicationContext) {
            XmlWebApplicationContext xwp = (XmlWebApplicationContext) applicationContext;
            tomname = xwp.getServletContext().getInitParameter("tomname");
        }
        logger.error("准备启动任务，当前tomcat名称是" + tomname);
        if (StringUtils.isNotBlank(tomname) && !"tomquartz".equals(tomname)) {
            logger.error("准备启动任务，当前tomcat名称是" + tomname + "，任务启动启动需要配置的名称是tomquartz，不启动任务");
            return false;
        }
        boolean hasjob1 = false;
        for (LiteJob job : jobs) {
            if ("1".equals(job.getId())) {
                hasjob1 = true;
                if (StringUtils.isNotBlank(job.getRunxx())) {
                    if (!job.getRunxx().equalsIgnoreCase(tomname)) {
                        logger.error("不启动启动任务，数据库中配置的tomcat名称是" + job.getRunxx() + ",当前tomcat名称是" + tomname);
                        return false;
                    }
                } else {
                    if (StringUtils.isNotBlank(tomname)) {
                        //update
                        LiteJob tu = new LiteJob();
                        tu.setId(job.getId());
                        tu.setRunxx(tomname);
                        this.updateSelective(tu);
                        logger.info("更新任务标记执行的TOMCAT名字为" + tomname);
                    }
                }
            }
        }
        //insert
        if (!hasjob1) {
            LiteJob tu = new LiteJob();
            tu.setId("1");
            tu.setRunxx(tomname);
            tu.setName("JOB执行标识");
            tu.setDetail(DateUtil.getStringDate() + "新增一条标识JOB，用来控制只能一个tomcat启动JOB");
            tu.setType("1");
            tu.setSpringid("无");
            tu.setStatus("0");
            tu.setCzdatetime("2017-01-01 00:00:00");
            this.insert(tu);
            logger.info("新增一条为1的记录");
        }
        return true;
    }

    /**
     * 暂停任务
     *
     * @throws SchedulerException
     */
    public void pauseJob(String id) throws BusinessException {
        try {
            LiteJob param = new LiteJob();
            param.setId(id);
            LiteJob job = this.getEntityById(param);
            liteQuartzService.doWhat(job, LiteQuartzService.DOJOB.PAUSE);
            //veQuartzService.pauseJob(job);
        } catch (Exception e) {
            logger.error("暂停任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "暂停任务失败:" + e.getMessage());
        }
    }

    /**
     * 恢复一个job
     *
     * @throws SchedulerException
     */
    public void resumeJob(String id) throws BusinessException {
        try {
            LiteJob param = new LiteJob();
            param.setId(id);
            LiteJob job = this.getEntityById(param);
            liteQuartzService.doWhat(job, LiteQuartzService.DOJOB.RESUME);
            //veQuartzService.resumeJob(job);
        } catch (Exception e) {
            logger.error("回复任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "回复任务失败:" + e.getMessage());
        }
    }

    /**
     * 更新job时间表达式
     *
     * @throws Exception
     */
    public void updateJobCron(String cronexpression, String id)
            throws BusinessException {
        try {

            LiteJob param = new LiteJob();
            param.setCronexpression(cronexpression);
            param.setId(id);
            this.updateSelective(param);
            param = new LiteJob();
            param.setId(id);
            LiteJob job = this.getEntityById(param);
            liteQuartzService.doWhat(job, LiteQuartzService.DOJOB.ADDUPDATE);
            //veQuartzService.updateJobCron(job);
        } catch (Exception e) {
            logger.error("修改任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "修改任务失败:" + e.getMessage());
        }
    }

    /**
     * 更新job循环执行时间间隔
     *
     * @throws Exception
     */
    private void updateJobRep(String repeatinterval, String id)
            throws BusinessException {
        try {
            LiteJob param = new LiteJob();
            param.setRepeatinterval(repeatinterval);
            param.setId(id);
            this.updateSelective(param);
            param = new LiteJob();
            param.setId(id);
            LiteJob job = this.getEntityById(param);
            liteQuartzService.doWhat(job, LiteQuartzService.DOJOB.ADDUPDATE);
            //veQuartzService.updateJobRep(job);
        } catch (Exception e) {
            logger.error("修改任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "修改任务失败:" + e.getMessage());
        }
    }

    /**
     * 开启一个任务
     *
     * @param id
     */
    public void addJob(String id) {
        try {
            LiteJob job = liteJobDao.selectByPrimaryKey(id);
            if (job != null && "0".equals(job.getStatus())) {
                logger.info("启动任务:" + job.getName() + ":" + job.getDetail());
                // 添加定时执行策略的job
                liteQuartzService.doWhat(job, LiteQuartzService.DOJOB.ADDUPDATE);
                //                if ("1".equals(job.getType())) {
                //                    veQuartzService.addJobCron(job);
                //                } else if ("2".equals(job.getType())) {// 添加重复执行的job
                //                    veQuartzService.addJobRep(job);
                //                }
                job.setStatus("1");
                liteJobDao.updateByPrimaryKey(job);
            }
        } catch (Exception e) {
            logger.error("启动任务失败", e);
            //throw new BusinessException(SystemErrorCode.SystemErrorCode,e,"启动任务失败:"+ e.getMessage());
        }
    }

    /**
     * 修改执行时间，根据类型分类修改
     *
     * @param id
     * @param time
     */
    public void changeTime(String id, String time) throws BusinessException {
        LiteJob job = liteJobDao.selectByPrimaryKey(id);
        if (StringUtils.equals("2", job.getType())) {
            job.setRepeatinterval(time);
            updateJobRep(time, id);
        } else {
            job.setCronexpression(time);
            updateJobCron(time, id);
        }
        liteJobDao.updateByPrimaryKey(job);
    }

    public ILiteJob getFirstDataJob() {
        return firstDataJob;
    }

    public void setFirstDataJob(ILiteJob firstDataJob) {
        this.firstDataJob = firstDataJob;
    }
}