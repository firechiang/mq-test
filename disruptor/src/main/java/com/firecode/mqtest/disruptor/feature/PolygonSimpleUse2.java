package com.firecode.mqtest.disruptor.feature;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.LockSupport;

import com.firecode.mqtest.disruptor.OrderHandler;
import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.firecode.mqtest.disruptor.helloword.OrderEventFactory;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 并行多边形操作简单使用（前面多个消费者并行执行，最后一个汇总结果，同一个消息在消费者间流转）
 * @author JIANG
 */
public class PolygonSimpleUse2 {
	
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		OrderEventFactory eventFactory = new OrderEventFactory();
		int ringBufferSize = 65536;
		ThreadFactory threadFactory = Executors.defaultThreadFactory();
		// 单生产模式
		ProducerType single = ProducerType.SINGLE;
		BusySpinWaitStrategy waitStrategy = new BusySpinWaitStrategy();
		/**
		 * @param eventFactory    事件工厂
		 * @param ringBufferSize  Ring缓冲区大小
		 * @param threadFactory   线程工厂
		 * @param single          生产者模式（单生产者模式）
		 * @param waitStrategy    等待策略（这个是给消费者用的，如果没有数据进行等待）
		 */
		Disruptor<OrderEvent> disruptor = new Disruptor<>(eventFactory,ringBufferSize,threadFactory,single,waitStrategy);
		OrderHandler o1 = new OrderHandler(1,true);
		OrderHandler o2 = new OrderHandler(2,false);
		OrderHandler o3 = new OrderHandler(3,false);
		OrderHandler o4 = new OrderHandler(4,false);
		OrderHandler o5 = new OrderHandler(5,false);
		// 配置o1,o4消费者并行执行
		disruptor.handleEventsWith(o1,o4);
		// o1执行完成以后执行o2
		disruptor.after(o1).handleEventsWith(o2);
		// o4执行完成以后执行o5
		disruptor.after(o4).handleEventsWith(o5);
		// 配置o2,o5消费者并行执行完成以后，最后执行o3
		disruptor.after(o2,o5).handleEventsWith(o3);
		// 启动
		disruptor.start();
		Thread currentThread = Thread.currentThread();
		// 生产者
		CompletableFuture.runAsync(() -> {
			Random r = new Random();
			for (int i = 0; i < 1; i++) {
				// 生产数据
				disruptor.publishEvent((OrderEvent event, long sequence)->{
					event.setOrderNo(r.nextLong());
				});
				LockSupport.unpark(currentThread);
			}
		});
		LockSupport.park();
		disruptor.shutdown();
		System.err.println("耗时："+(System.currentTimeMillis()-time));
	}
}
