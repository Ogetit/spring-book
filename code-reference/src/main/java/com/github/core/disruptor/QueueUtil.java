package com.github.core.disruptor;

import org.github.core.modules.service.SpringContextUtil;

/**
 * 队列工具类
 * @author 章磊
 *
 */
public class QueueUtil {
	/**
	 * 放入对列
	 * @param type 消息类型，通过spring的bean的ID获得
	 * @param msg 消息
	 */
	public static void put(String type, Object msg) {
		DisruptorHelper disruptorHelper =(DisruptorHelper) SpringContextUtil.getBean(DisruptorHelper.class);
		disruptorHelper.produce(type, msg);
	}
}
