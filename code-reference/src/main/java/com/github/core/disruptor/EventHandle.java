package com.github.core.disruptor;
import com.lmax.disruptor.EventHandler;
import org.github.core.modules.service.SpringContextUtil;

/**
 * 事件处理，消费者
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  zhanglei
 * @version  [版本号, Jul 9, 2012]
 * @see  [相关类/方法]
 * @since  [GITHUB]
 */
public class EventHandle implements EventHandler<Event>{
	/**
	 * 线程池
	 */
	private WorkPool workPool;
	/**
	 * 构造
	 * @param size 线程大小
	 */
	public EventHandle(int size){
		workPool =  new WorkPool(size);
	}
	
	public WorkPool getWorkPool() {
		return workPool;
	}

	/**
	 * 事件
	 * @param event .
	 * @param sequence .
	 * @param endOfBatch .
	 * @throws Exception 没有得到任务
	 */
	@Override
	public void onEvent(Event event, long sequence, boolean endOfBatch) throws Exception {
		// TODO Auto-generated method stub
		try{
			Task task = (Task)SpringContextUtil.getBean(event.getEventType());
			if(SpringContextUtil.isSingleton(event.getEventType())){
				throw new Exception("任务必须是多例");
			}
			Event eventNew = new Event();
			eventNew.setData(event.getData());
			eventNew.setEventType(event.getEventType());
			eventNew.setId(event.getId());
			eventNew.setProcessTime(event.getProcessTime());
			eventNew.setTime(event.getTime());
			event.clear();
			task.setEvent(eventNew);
			workPool.execute(task);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
