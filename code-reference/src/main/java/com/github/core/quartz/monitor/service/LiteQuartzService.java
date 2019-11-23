package com.github.core.quartz.monitor.service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.github.core.modules.exception.BusinessException;
import org.github.core.modules.exception.SystemErrorCode;
import org.github.core.modules.service.SpringContextUtil;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.github.core.quartz.monitor.entity.LiteJob;
import com.github.core.quartz.monitor.entity.LiteTrigger;

/**
 * @Author ouyangjie
 * @Description 轻量型任务测试
 * @Date 4:00 PM 2018/12/9
 */
@Service
public class LiteQuartzService {
    private final Logger logger = LoggerFactory.getLogger(LiteQuartzService.class);

    private Logger getLogger() {
        MDC.put("filename", "job/job");
        return logger;
    }

    @Autowired(required = false)
    private SchedulerFactoryBean localQuartzScheduler;

    public static enum DOJOB {
        ADDUPDATE, STATE, PAUSE, RESUME, EXEC, DEL, ADD
    }

    /**
     * 调用job接口执行
     *
     * @param job
     * @param dojob
     *
     * @return
     *
     * @throws Exception
     */
    public String doWhat(LiteJob job, DOJOB dojob) throws Exception {
        Scheduler scheduler = localQuartzScheduler.getScheduler();
        getLogger().error("JOB操作" + job.getId() + "," + job.getName() + "," + job.getSpringid() + "，执行的动作=" + dojob
                .name());
        String state = "0";
        if (dojob.equals(DOJOB.ADDUPDATE)) {
            // 如果存在先删除，然后重新添加
            addUpdate(scheduler, job, true);
        } else if (dojob.equals(DOJOB.ADD)) {
            // 只添加，如果存在则不做任务操作
            addUpdate(scheduler, job, false);
        } else if (dojob.equals(DOJOB.STATE)) {
            state = jobState(scheduler, job);
        } else if (dojob.equals(DOJOB.PAUSE)) {
            jobPause(scheduler, job);
        } else if (dojob.equals(DOJOB.RESUME)) {
            jobresume(scheduler, job);
        } else if (dojob.equals(DOJOB.EXEC)) {
            jobExec(scheduler, job);
        } else if (dojob.equals(DOJOB.DEL)) {
            jobDel(scheduler, job);
        }
        getLogger().error("JOB操作" + job.getId() + ",执行" + dojob.name() + ",完成");
        return state;
    }

    /**
     * 添加或更细job和trigger
     *
     * @param scheduler
     * @param job
     * @param update
     *
     * @throws Exception
     */
    private void addUpdate(Scheduler scheduler, LiteJob job, boolean update) throws Exception {
        if ("2".equals(job.getType())) {
            addUpdateSimple(scheduler, job, update);
        } else {
            addUpdateCron(scheduler, job, update);
        }
    }

    /**
     * 开启一个每天规定时间执行的job
     *
     * @param job
     *
     * @throws BusinessException
     */
    private void addJobCron(LiteJob job) throws BusinessException {
        try {
            // new的这个类与spring配置的是一致
            MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean =
                    new MethodInvokingJobDetailFactoryBean();
            // 取一个名字，就是bean的id
            methodInvokingJobDetailFactoryBean.setName(job.getName());
            // 设置执行的目标对象,我们实现的对象
            methodInvokingJobDetailFactoryBean
                    .setTargetObject(SpringContextUtil.getBean(job
                            .getSpringid()));
            // 执行目标对象的execute方法
            methodInvokingJobDetailFactoryBean.setTargetMethod("execute");
            // 不能同步执行
            methodInvokingJobDetailFactoryBean.setConcurrent(false);
            // 初始化上面的配置
            methodInvokingJobDetailFactoryBean.afterPropertiesSet();

            // 动态
            JobDetail jobDetail = (JobDetail) methodInvokingJobDetailFactoryBean.getObject();
            jobDetail.getJobDataMap().put("scheduleJob", job);
            // 策略
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            cronTriggerFactoryBean.setJobDetail(jobDetail);
            cronTriggerFactoryBean.setCronExpression(job
                    .getCronexpression());
            cronTriggerFactoryBean.setName(job.getName() + "Trigger");
            cronTriggerFactoryBean.setDescription(job.getDetail());
            cronTriggerFactoryBean.getJobDataMap().put("scheduleJob", job);
            cronTriggerFactoryBean.afterPropertiesSet();
            localQuartzScheduler.getScheduler().addJob(jobDetail, true);
            localQuartzScheduler.getScheduler().scheduleJob(
                    cronTriggerFactoryBean.getObject());
        } catch (Exception e) {
            getLogger().error("启动任务失败", e);
            // throw new BusinessException(SystemErrorCode.SystemErrorCode,e,"启动任务失败:"+ e.getMessage());
        }
    }

    /**
     * 如果update为true 表示 如果存在则先删除然后，添加
     *
     * @param job
     * @param update
     *
     * @throws Exception
     */
    private void addUpdateSimple(Scheduler scheduler, LiteJob job, boolean update) throws Exception {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail != null) {
            if (update) {
                jobDel(scheduler, job);
            } else {
                return;
            }
        }
        // 得到具体的和指定作业相关的 JobDetail 对象
        // 要调用建造器的 build()方法，才能实例当前作业的具体作业对象
        jobDetail = JobBuilder.newJob(LiteJobService.class).withIdentity(job.getName(), job.getGroup())
                .withDescription(job.getDetail()).build();

        // 得到作业的参数对象
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put(LiteJob.JOB_DATA_NAME, job);

        TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), job.getGroup());

        Date startdate = new Date();
        startdate = getRealStartDate(job, startdate);

        // 实例化触发器对象（定义执行的时间和执行的周期）
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever()
                        .withIntervalInMilliseconds(NumberUtils.toLong(job.getRepeatinterval())))
                .withDescription(job.getDetail()).startAt(startdate).build();

        // 添加调度作业（将具体的作业和触发器添加到作业中）
        scheduler.scheduleJob(jobDetail, simpleTrigger);
    }

    /**
     * update 为true 表示 如果存在则先删除 再添加
     *
     * @param job
     * @param update
     *
     * @throws Exception
     */
    private void addUpdateCron(Scheduler scheduler, LiteJob job, boolean update) throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), job.getGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        // 修改时间无效，先删除再创建
        if (trigger != null) {
            if (update) {
                jobDel(scheduler, job);
            } else {
                return;
            }
        }
        // 不存在，创建一个
        JobDetail jobDetail = JobBuilder.newJob(LiteJobService.class).withIdentity(job.getName(), job.getGroup())
                .withDescription(job.getDetail()).build();
        jobDetail.getJobDataMap().put(LiteJob.JOB_DATA_NAME, job);
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronexpression());
        // 按新的cronExpression表达式构建一个新的trigger
        trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder)
                .withDescription(job.getDetail()).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 开启一个循环执行的Job
     *
     * @param job
     *
     * @throws BusinessException
     */
    private void addJobRep(LiteJob job, boolean update) throws BusinessException {
        try {
            Scheduler scheduler = localQuartzScheduler.getScheduler();
            JobKey jobKey = JobKey.jobKey(job.getName(), Scheduler.DEFAULT_GROUP);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail != null) {
                if (update) {
                    scheduler.deleteJob(jobKey);
                } else {
                    return;
                }
            }
            MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean =
                    new MethodInvokingJobDetailFactoryBean();
            methodInvokingJobDetailFactoryBean.setName(job.getName());
            methodInvokingJobDetailFactoryBean.setTargetObject(SpringContextUtil.getBean(job.getSpringid()));
            methodInvokingJobDetailFactoryBean.setTargetMethod("execute");
            methodInvokingJobDetailFactoryBean.setConcurrent(false);
            methodInvokingJobDetailFactoryBean.afterPropertiesSet();

            // 动态
            jobDetail = methodInvokingJobDetailFactoryBean.getObject();
            jobDetail.getJobDataMap().put("scheduleJob", job);
            Date startdate = new Date();
            startdate = getRealStartDate(job, startdate);

            TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), Scheduler.DEFAULT_GROUP);
            SimpleTrigger simpleTrigger =
                    TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(// 实例化触发器对象（定义执行的时间和执行的周期）
                            SimpleScheduleBuilder.simpleSchedule().repeatForever()
                                    .withIntervalInMilliseconds(NumberUtils.toLong(job.getRepeatinterval())))
                            .startAt(startdate).build();
            // 添加调度作业（将具体的作业和触发器添加到作业中）
            scheduler.scheduleJob(jobDetail, simpleTrigger);

        } catch (Exception e) {
            getLogger().error("启动任务失败", e);
            //throw new BusinessException(SystemErrorCode.SystemErrorCode,e,"启动任务失败:"+ e.getMessage());
        }
    }

    private Date getRealStartDate(LiteJob job, Date startdate) {
        if (NumberUtils.toInt(job.getStartdelay()) > 0) {
            GregorianCalendar gc = (GregorianCalendar) java.util.Calendar.getInstance();
            gc.setTime(new Date());
            gc.add(java.util.Calendar.MILLISECOND, NumberUtils.toInt(job.getStartdelay()));
            startdate = gc.getTime();
        }
        return startdate;
    }

    /**
     * 获得job的状态
     *
     * @param scheduler
     * @param job
     *
     * @return
     *
     * @throws Exception
     */
    private String jobState(Scheduler scheduler, LiteJob job) throws Exception {
        LiteTrigger t = new LiteTrigger();
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), job.getGroup());
        Trigger trigger = scheduler.getTrigger(triggerKey);
        // 不存在，创建一个
        if (null == trigger) {
            return null;
        } else {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            t.setTriggerState(triggerState.name());
            t.setNextFireTime(trigger.getNextFireTime());
            return t.toString();
        }
    }

    /**
     * 暂停job
     *
     * @param scheduler
     * @param job
     *
     * @throws Exception
     */
    private void jobPause(Scheduler scheduler, LiteJob job) throws Exception {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复执行job
     *
     * @param scheduler
     * @param job
     *
     * @throws Exception
     */
    private void jobresume(Scheduler scheduler, LiteJob job) throws Exception {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        scheduler.resumeJob(jobKey);
    }

    /**
     * 立即执行job
     *
     * @param scheduler
     * @param job
     *
     * @throws Exception
     */
    private void jobExec(Scheduler scheduler, LiteJob job) throws Exception {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        scheduler.triggerJob(jobKey);
    }

    /**
     * 删除job
     *
     * @param scheduler
     * @param job
     *
     * @throws Exception
     */
    private void jobDel(Scheduler scheduler, LiteJob job) throws Exception {
        JobKey jobKey = JobKey.jobKey(job.getName(), job.getGroup());
        scheduler.deleteJob(jobKey);
    }

    /**
     * 暂停任务
     *
     * @throws SchedulerException
     */
    private void pauseJob(LiteJob job) throws BusinessException {
        try {
            Scheduler scheduler = localQuartzScheduler.getScheduler();
            JobKey jobKey = JobKey.jobKey(job.getName(), Scheduler.DEFAULT_GROUP);
            scheduler.pauseJob(jobKey);
        } catch (Exception e) {
            getLogger().error("暂停任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "暂停任务失败:" + e.getMessage());
        }
    }

    /**
     * 恢复一个job
     *
     * @throws SchedulerException
     */
    private void resumeJob(LiteJob job) throws BusinessException {
        try {
            Scheduler scheduler = localQuartzScheduler.getScheduler();
            JobKey jobKey = JobKey.jobKey(job.getName(), Scheduler.DEFAULT_GROUP);
            scheduler.resumeJob(jobKey);
        } catch (Exception e) {
            getLogger().error("回复任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "回复任务失败:" + e.getMessage());
        }
    }

    /**
     * 更新job时间表达式
     *
     * @throws Exception
     */
    private void updateJobCron(LiteJob job) throws BusinessException {
        try {
            Scheduler scheduler = localQuartzScheduler.getScheduler();
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeys) {
                List triggers = scheduler.getTriggersOfJob(jobKey);
                for (Object trigger : triggers) {
                    if (trigger instanceof CronTrigger) {
                        LiteJob oldJob = (LiteJob) ((Trigger) trigger).getJobDataMap().get("scheduleJob");
                        if (StringUtils.equals(job.getId(), oldJob.getId())) {
                            CronTrigger cronTrigger = (CronTrigger) trigger;
                            TriggerKey triggerKey = cronTrigger.getKey();
                            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                                    .cronSchedule(job.getCronexpression());
                            trigger = ((CronTrigger) trigger).getTriggerBuilder().withIdentity(triggerKey)
                                    .withSchedule(scheduleBuilder).build();
                            scheduler.rescheduleJob(triggerKey, (CronTrigger) trigger);
                        }
                    }
                }
            }
        } catch (Exception e) {
            getLogger().error("修改任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "修改任务失败:" + e.getMessage());
        }
    }

    /**
     * 更新job循环执行时间间隔
     *
     * @throws Exception
     */
    private void updateJobRep(LiteJob job) throws BusinessException {
        try {
            Scheduler scheduler = localQuartzScheduler.getScheduler();
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeys) {
                List triggers = scheduler
                        .getTriggersOfJob(jobKey);
                for (Object trigger : triggers) {
                    if (trigger instanceof SimpleTrigger) {
                        LiteJob oldJob = (LiteJob) ((Trigger) trigger).getJobDataMap().get("scheduleJob");
                        if (StringUtils.equals(job.getId(), oldJob.getId())) {
                            SimpleTrigger repTrigger = (SimpleTrigger) trigger;
                            TriggerKey triggerKey = repTrigger.getKey();
                            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                                    .repeatSecondlyForever(Integer.parseInt(job.getRepeatinterval()) / 1000);
                            trigger = ((SimpleTrigger) trigger).getTriggerBuilder().withIdentity(triggerKey)
                                    .withSchedule(scheduleBuilder).build();
                            scheduler.rescheduleJob(triggerKey, (SimpleTrigger) trigger);
                        }
                    }
                }
            }
        } catch (Exception e) {
            getLogger().error("修改任务失败", e);
            throw new BusinessException(SystemErrorCode.SystemErrorCode, e, "修改任务失败:" + e.getMessage());
        }
    }
}
