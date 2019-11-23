package com.github.core.disruptor;

/**
 * 
 * Disruptor 高并发框架，生产和消费的关系 提供ringbuffer环状算法，比队列性能更高 <功能详细描述>
 * 
 * @author zhanglei
 * @version [版本号, Jul 9, 2012]
 * @see [相关类/方法]
 * @since [GITHUB]
 */
public class DisruptorHelper {
	/**
	 * 线程池
	 */
	private WorkPool workPool;
	/**
	 * 处理器
	 */
	public DisruptorService disruptor;
	private int workerpoolSize;
	private int ringBufferSize;
	/**
	 * 构造
	 */
	public void init() {
		if (workerpoolSize == 0) {
			throw new RuntimeException("请正确配置workerpoolSize");
		}
		if (ringBufferSize == 0) {
			throw new RuntimeException("请正确配置ringBufferSize");
		}
		EventHandle eventHandle = new EventHandle(workerpoolSize);
		workPool = eventHandle.getWorkPool();
		disruptor = new DisruptorService(ringBufferSize * 1024 , "异步处理框架", eventHandle);
	}

	public WorkPool getWorkPool() {
		return workPool;
	}

	/**
	 * 将状态报告放入资源队列，等待处理
	 * 
	 * @param type 消息类型，通过spring的bean的ID获得
	 * @param msg 消息
	 */
	public void produce(String type, Object msg) {
		disruptor.produce(type, msg);
	}

	public DisruptorService getDisruptor() {
		return disruptor;
	}

	public void setWorkerpoolSize(int workerpoolSize) {
		this.workerpoolSize = workerpoolSize;
	}

	public void setRingBufferSize(int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
	}
}
