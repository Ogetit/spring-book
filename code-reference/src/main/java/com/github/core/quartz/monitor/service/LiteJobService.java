package com.github.core.quartz.monitor.service;

import com.github.core.quartz.monitor.entity.LiteJob;
import com.github.core.quartz.monitor.ILiteJob;
import com.github.core.quartz.monitor.ILiteJobNew;
import com.github.core.quartz.monitor.dao.LiteJobDao;

import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.github.core.modules.service.SpringContextUtil;

/**
 * Created by java on 2017/7/3.
 * JOB统一由他来执行
 */
@Component
@DisallowConcurrentExecution
public class LiteJobService extends QuartzJobBean {
    private static Logger logger = LoggerFactory.getLogger(LiteJobService.class);

    private static Logger getLogger() {
        MDC.put("filename", "job/job");
        return logger;
    }

    @Override
    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
        try {
            long t = System.currentTimeMillis();
            LiteJob jobBean = (LiteJob) ctx.getJobDetail().getJobDataMap().get(LiteJob.JOB_DATA_NAME);
            if (jobBean == null) {
                getLogger().error("JOB执行失败对象不存在,不执行");
                return;
            }
            if (StringUtils.isNotBlank(jobBean.getId())) {
                LiteJobDao liteJobDao = SpringContextUtil.getBean(LiteJobDao.class);
                LiteJob dbjob = liteJobDao.selectByPrimaryKey(jobBean.getId());
                if (dbjob != null) {
                    if (!"1".equals(dbjob.getStatus())) {
                        getLogger().error("表中状态不是1，删除JOB");
                        delJob(jobBean);
                        return;
                    }
                }
            }
            getLogger()
                    .info("JOB开始执行，获取到的job对象" + jobBean.getId() + "," + jobBean.getName() + "," + jobBean.getSpringid()
                            + "," + jobBean.getDetail() + "," + jobBean.getData());
            Object o = SpringContextUtil.getBean(jobBean.getSpringid());
            if (o instanceof ILiteJob) {
                ((ILiteJob) o).execute();
            } else if (o instanceof ILiteJobNew) {
                int state = ((ILiteJobNew) o).execute(jobBean);
                if (state == -1) {
                    getLogger().error("JOB执行后返回-1需要删除");
                    delJob(jobBean);
                }
            }
            getLogger().info("JOB执行结束" + jobBean.getId() + ",耗时" + (System.currentTimeMillis() - t));
        } catch (Exception e) {
            getLogger().error("JOB执行异常", e);
        }
    }

    private void delJob(LiteJob jobBean) {
        try {
            getLogger().error("删除JOB" + jobBean.getId() + "," + jobBean.getName() + "," + jobBean.getData());
            LiteQuartzService liteQuartzService = SpringContextUtil.getBean(LiteQuartzService.class);
            liteQuartzService.doWhat(jobBean, LiteQuartzService.DOJOB.DEL);
        } catch (Exception e) {
            getLogger().error("JOB删除失败", e);
        }
    }
}
