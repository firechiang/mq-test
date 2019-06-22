package com.firecode.mqtest.disruptor.helloword;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 测试
 * @author JIANG
 */
public class OrderEventMain {
	
	static final int index = 0;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		OrderEventFactory eventFactory = new OrderEventFactory();
		int ringBufferSize = 65536;
		ThreadFactory threadFactory = Executors.defaultThreadFactory();
		ProducerType single = ProducerType.SINGLE;
		// 效率最低的策略，但它对CPU的消耗最小并且在各种不同的环境中能提供更加一致的性能表现
		BlockingWaitStrategy bWaitStrategy = new BlockingWaitStrategy();
		// 性能和 BlockingWaitStrategy 差不多，对CPU的消耗也类似，但对生产者线程的影响最小，适合用于异步日志类似的场景
		SleepingWaitStrategy sWaitStrategy = new SleepingWaitStrategy();
		// 效率最高的策略，同时对CPU利用率也极高，适合用于低延迟的系统，在要求极高性能且事件处理数小于CPU逻辑核心数的场景中，推荐使用此策略；例如：CPU开启超线程的特性
		YieldingWaitStrategy yWaitStrategy = new YieldingWaitStrategy();
		/**
		 * @param eventFactory    事件工厂
		 * @param ringBufferSize  Ring缓冲区大小
		 * @param threadFactory   线程工厂
		 * @param single          生产者模式（单生产者模式）
		 * @param waitStrategy    等待策略（这个是给消费者用的，如果没有数据进行等待）
		 */
		Disruptor<OrderEvent> disruptor = new Disruptor<>(eventFactory,ringBufferSize,threadFactory,single,bWaitStrategy);
		/**
		 * 配置消费者
		 */
		disruptor.handleEventsWith(new OrderEventHandler());
		/**
		 * 启动消费（注意：要先启动消费者，在使用生产者，它的性能才能最大化）
		 */
		disruptor.start();
		/**
		 * 获取数据存储缓冲区
		 */
		RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
		/**
		 * 创建生产者
		 */
		OrderEventProducer producer = new OrderEventProducer(ringBuffer);
		/**
		 * 创建字节缓冲区
		 */
		ByteBuffer bf = ByteBuffer.allocate(8);
		/**
		 * 开始生产数据
		 */
		for(long i=0;i<100000;i++){
			// 在第0个位置添加数据i
			bf.putLong(index, i);
			producer.addData(bf);
		}
		disruptor.shutdown();
	}
}
