package com.firecode.mqtest.disruptor;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import org.junit.Test;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorTest {
	/**
	 * 10亿
	 */
	private static final int MILLION = 1000000000;

	@SuppressWarnings("unused")
	@Test
	public void test() {
		Thread currentThread = Thread.currentThread();
		int ringBufferSize = 65536;
		Disruptor<String> disruptor = new Disruptor<String>(new StringTestEventFactory(),
				                                            ringBufferSize,Executors.defaultThreadFactory(),
				                                            ProducerType.SINGLE,//单生产者
				                                            new YieldingWaitStrategy());
		long time = System.currentTimeMillis();
		/**
		 * 消费者
		 */
		disruptor.handleEventsWith(new StringTestEventHandler(currentThread,time));
		/**
		 * 启动消费（注意：要先启动消费者，在使用生产者，它的性能才能最大化）
		 */
		disruptor.start();	
		/**
		 * 生产者
		 */
		new Thread(()-> {
			RingBuffer<String> ringBuffer = disruptor.getRingBuffer();
			for (int i = 0; i < MILLION; i++) {
				long sequence = ringBuffer.next();
				String string = ringBuffer.get(sequence);
				ringBuffer.publish(sequence);
			}
		}).start();
		LockSupport.park();
	}
	
	private static class StringTestEventFactory implements EventFactory<String> {

		@Override
		public String newInstance() {
			
			return "测试";
		}
	}
	
	private static class StringTestEventHandler implements EventHandler<String> {
		
		private final Thread currentThread;
		private final long time;
		private int count;
		
		private StringTestEventHandler(Thread currentThread,long time){
			this.currentThread = currentThread;
			this.time = time;
		}

		@Override
		public void onEvent(String event, long sequence, boolean endOfBatch) throws Exception {
			count ++;
			//System.err.println(event+": "+count);
			if(count >= MILLION) {
				System.err.println("消费完成，耗时："+((System.currentTimeMillis() - time) / 1000));
				LockSupport.unpark(currentThread);
			}
		}
	}

}
