package com.github.core.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.github.core.modules.utils.Threads;

import java.util.Date;
import java.util.concurrent.ThreadFactory;

/**
 * 异步框架服务
 * @author 章磊
 *
 */
public class DisruptorService {
    private Logger logger= LoggerFactory.getLogger(DisruptorService.class);
    /**
    * ringbuffer容量，最好是2的N次方
    */
   private RingBuffer<Event> ringBuffer;
 
   private String name;
   private Disruptor<Event> disruptor;
   private int bufferSize;
   /**
    * 构造
    * @param bufferSize 队列大小
    * @param name 名称
    * @param handler 事件处理
    */
   public DisruptorService(int bufferSize,String name,EventHandler<Event> handler){
       this.name = name;
       this.bufferSize = bufferSize;
       //这种策略在linux下，没有压力下CPU占用率小，其他的策略都在200%左右
       ThreadFactory threadFactory = Threads.buildJobFactory("异步框架服务-%d");
       disruptor = new Disruptor<Event>(Event.EVENT_FACTORY, bufferSize, threadFactory, ProducerType.MULTI, new BlockingWaitStrategy());
       disruptor.handleEventsWith(handler);
       ringBuffer = disruptor.start();
   }

   public long getBufferSize(){
	   return bufferSize;
   }
   public long getLeftqueue(){
	   return ringBuffer.remainingCapacity();
   }
   /**
    * 关闭
    */
   public  void shutdown(){
       disruptor.shutdown();
   }
   /**
    * 放入队列
    * @param type 消息类型
    * @param msg  消息
    */
   private void produce0(String type,Object msg) {
       //获取下一个序列号
       long sequence = ringBuffer.next();//如果满了这里会阻塞
       //将状态报告存入ringBuffer的该序列号中
       Event info = ringBuffer.get(sequence);
       info.setData(msg);
       info.setEventType(type);
       info.setTime(new Date());
       info.setId(sequence+"");
       //通知消费者该资源可以消费
       ringBuffer.publish(sequence);
       
//       logger.debug("["+name+"]["+sequence+"]push="+info.toString());
   }

   /**
    * 将状态报告放入资源队列，等待处理
    * @param type 消息类型
    * @param msg  消息
    */
   public  void produce(String type,Object msg)  {
	// if capacity less than 10%, don't use ringbuffer anymore   
	   if(ringBuffer.remainingCapacity() < bufferSize * 0.01) {
           logger.warn("目前队列已经小于1%");
	   }   
       produce0(type,msg);
   }
}
