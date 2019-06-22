package com.firecode.mqtest.disruptor.feature;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.firecode.mqtest.disruptor.OrderExceptionHandler;
import com.firecode.mqtest.disruptor.WorkOrderHandler;
import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.firecode.mqtest.disruptor.helloword.OrderEventFactory;
import com.firecode.mqtest.disruptor.helloword.OrderEventProducer;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 多生产者，多消费者简单实用（就是队列模型简单实用）
 * @author JIANG
 */
public class ManyConsumer {
	
	public static void main(String[] args) throws InterruptedException {
		long time = System.currentTimeMillis();
		OrderEventFactory eventFactory = new OrderEventFactory();
		int ringBufferSize = 65536;
		YieldingWaitStrategy waitStrategy = new YieldingWaitStrategy();
		// 多生产者模式
		ProducerType producerType = ProducerType.MULTI;
		// 创建缓冲区
		RingBuffer<OrderEvent> buffer = RingBuffer.create(producerType, eventFactory, ringBufferSize, waitStrategy);
		// 创建序号屏障
		SequenceBarrier newBarrier = buffer.newBarrier();
		// 创建消费者
		WorkOrderHandler o1 = new WorkOrderHandler(1,false);
		// 创建消费者
		WorkOrderHandler o2 = new WorkOrderHandler(2,false);
		/**
		 * 构建多消费者工作池
		 * @param ringBuffer         缓冲区       
		 * @param sequenceBarrier    序号屏障
		 * @param exceptionHandler   错误处理器
		 * @param workHandlers...    消费者
		 */
		WorkerPool<OrderEvent> workerPool = new WorkerPool<>(buffer, newBarrier, new OrderExceptionHandler(),o1,o2);
		// 设置多消费者的sequence（消费序号）用于统计消费进度，并设置到RingBuffer中，以供生产者占位填充数据
		buffer.addGatingSequences(workerPool.getWorkerSequences());
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		// 启动
		workerPool.start(newFixedThreadPool);
		
		// 生产者投递消息
		Thread currentThread = Thread.currentThread();
		CompletableFuture.runAsync(() -> {
			// 生产者
			OrderEventProducer producer = new OrderEventProducer(buffer);
			// 创建字节缓冲区
			ByteBuffer bf = ByteBuffer.allocate(8);
			// 开始生产数据
			for(long i=0;i<1;i++){
				// 在第0个位置添加数据i
				bf.putLong(0, i);
				// 发送消息
				producer.addData(bf);
			}
			LockSupport.unpark(currentThread);
		});
		LockSupport.park();
		newFixedThreadPool.shutdown();
		newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS);
		newFixedThreadPool.shutdownNow();
		System.err.println("耗时："+(System.currentTimeMillis()-time));
	}
}
