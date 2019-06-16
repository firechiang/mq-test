package com.firecode.mqtest.disruptor.helloword;

import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 测试
 * @author JIANG
 */
public class OrderEventMain {
	
	static final int index = 0;
	
	public static void main(String[] args) {
		OrderEventFactory eventFactory = new OrderEventFactory();
		int ringBufferSize = 65536;
		ThreadFactory threadFactory = Executors.defaultThreadFactory();
		ProducerType single = ProducerType.SINGLE;
		BlockingWaitStrategy waitStrategy = new BlockingWaitStrategy();
		/**
		 * @param eventFactory    事件工厂
		 * @param ringBufferSize  Ring缓冲区大小
		 * @param threadFactory   线程工厂
		 * @param single          生产者模式（单生产者模式）
		 * @param waitStrategy    等待策略
		 */
		Disruptor<OrderEvent> disruptor = new Disruptor<>(eventFactory,ringBufferSize,threadFactory,single,waitStrategy);
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
