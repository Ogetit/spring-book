package com.github.core.disruptor;

import com.lmax.disruptor.EventFactory;

import java.util.Date;

/**
 * 
 * 当客户对请求后放入队列后会产生事件 <功能详细描述>
 * 
 * @author 章磊
 * @version [版本号, Jul 9, 2012]
 * @see [相关类/方法]
 * @since [GITHUB]
 */
public class Event {
	/**
	 * 唯一编号
	 */
	private String id;
	/**
	 * 事件类型，根据不同类型有不同处理
	 */
	private String eventType;

	/**
	 * 待处理数据
	 */
	private Object data;

	/**
	 * 事件触发时间
	 */
	private Date time;

	/**
	 * 被处理的时间
	 */
	private String processTime;
	/**
	 * 清空数据
	 */
	public void clear(){
		id = null;
		eventType = null;
		data = null;
		time = null;
		processTime = null;
	}
	
	/**
	 * 事件工厂
	 */
	public final static EventFactory<Event> EVENT_FACTORY = new EventFactory<Event>() {
		public Event newInstance() {
			return new Event();
		}
	};


	/**
	 *  
	 * @return .
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 *  
	 * @param eventType .
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * 
	 * @return .
	 */
	public Object getData() {
		return data;
	}

	/**
	 *  
	 * @param data .
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 *  
	 * @return .
	 */
	public Date getTime() {
		return time;
	}

	/**
	 *  
	 * @param time .
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 *  
	 * @return .
	 */
	public String getProcessTime() {
		return processTime;
	}

	/**
	 *  
	 * @param processTime 
	 */
	public void setProcessTime(String processTime) {
		this.processTime = processTime;
	}

	/**
	 *  
	 * @return .
	 */
	public String getId() {
		return id;
	}

	/**
	 *  
	 * @param id .
	 */
	public void setId(String id) {
		this.id = id;
	}


}
