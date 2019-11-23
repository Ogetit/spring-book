package com.github.core.quartz.monitor.entity;

import org.github.core.modules.utils.DateUtil;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.util.Date;

public class LiteTrigger {
	private TriggerKey key;

	private JobKey jobkey;

	private String description;

	private String calendarName;

	private JobDataMap jobDataMap;

	private int priority;

	private boolean mayFireAgain;

	private Date startTime;

	private Date endTime;

	private Date nextFireTime;

	private Date previousFireTime;

	private Date finalFireTime;

	private int misfireInstruction;
	/**时差*/
	private long shicha;

	// None：Trigger已经完成，且不会在执行，或者找不到该触发器，或者Trigger已经被删除
	// NORMAL:正常状态
	// PAUSED：暂停状态
	// COMPLETE：触发器完成，但是任务可能还正在执行中
	// BLOCKED：线程阻塞状态
	// ERROR：出现错误
	private String triggerState;
	public String getStateZw(){
		if("None".equals(triggerState)){
			return "不会再执行";
		}else if("NORMAL".equals(triggerState)){
			return "正常状态";
		}else if("PAUSED".equals(triggerState)){
			return "暂停状态";
		}else if("COMPLETE".equals(triggerState)){
			return "触发器完成";
		}else if("BLOCKED".equals(triggerState)){
			return "线程阻塞状态";
		}else if("ERROR".equals(triggerState)){
			return "出现错误";
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "{'triggerState':'" + getTriggerState() + "','nextFireTime':'" + DateUtil.dateToStrLong(getNextFireTime()) + "'}";
	}
	public String getPreviousFireTimeStr(){
		return DateUtil.dateToStrLong(previousFireTime);
	}
	public String getNextFireTimeStr(){
		return DateUtil.dateToStrLong(nextFireTime);
	}
	public String getStartTimeStr(){
		return DateUtil.dateToStrLong(startTime);
	}
	public String getTriggerState() {
		return triggerState;
	}

	public void setTriggerState(String triggerState) {
		this.triggerState = triggerState;
	}

	public TriggerKey getKey() {
		return key;
	}

	public void setKey(TriggerKey key) {
		this.key = key;
	}

	public JobKey getJobkey() {
		return jobkey;
	}

	public void setJobkey(JobKey jobkey) {
		this.jobkey = jobkey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCalendarName() {
		return calendarName;
	}

	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}

	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public boolean isMayFireAgain() {
		return mayFireAgain;
	}

	public void setMayFireAgain(boolean mayFireAgain) {
		this.mayFireAgain = mayFireAgain;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	public Date getFinalFireTime() {
		return finalFireTime;
	}

	public void setFinalFireTime(Date finalFireTime) {
		this.finalFireTime = finalFireTime;
	}

	public int getMisfireInstruction() {
		return misfireInstruction;
	}

	public void setMisfireInstruction(int misfireInstruction) {
		this.misfireInstruction = misfireInstruction;
	}

	public long getShicha() {
		return shicha;
	}

	public void setShicha(long shicha) {
		this.shicha = shicha;
	}
}
