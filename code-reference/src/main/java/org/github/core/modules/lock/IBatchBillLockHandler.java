package org.github.core.modules.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface IBatchBillLockHandler {

	/**
	 * 获取锁 如果锁可用 立即返回true， 否则立即返回返回false
	 * 
	 * @author
	 * @param billIdentify
	 * @return
	 */
	boolean tryLock(IBillIdentify billIdentify);

	/**
	 * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
	 * 
	 * @author
	 * @param billIdentify
	 * @param timeout
	 * @param unit
	 * @return
	 */
	boolean tryLock(IBillIdentify billIdentify, long timeout, TimeUnit unit);

	/**
	 * 如果锁空闲立即返回 获取失败 一直等待
	 * 
	 * @author
	 * @param billIdentify
	 */
	void lock(IBillIdentify billIdentify);

	/**
	 * 释放锁
	 * 
	 * @author
	 * @param billIdentify
	 */
	void unLock(IBillIdentify billIdentify);

	/**
	 * 批量释放锁
	 * 
	 * @author
	 * @param billIdentifyList
	 */
	void unLock(List<IBillIdentify> billIdentifyList);

}