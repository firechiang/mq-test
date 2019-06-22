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
 * 并行操作简单使用（每条消息多个消费者都能收到）
 * @author JIANG
 */
public class ParallelSimpleUse {
	
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
		// 配置2个消费者并行执行（就是2个消费者同时执行）
		//disruptor.handleEventsWith(new OrderHandler(1,true),new OrderHandler(2,false));
		disruptor.handleEventsWith(new OrderHandler(1,true));
		disruptor.handleEventsWith(new OrderHandler(2,false));
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
