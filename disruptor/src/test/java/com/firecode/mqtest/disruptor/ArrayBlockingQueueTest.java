package com.firecode.mqtest.disruptor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.LockSupport;

import org.junit.Test;

public class ArrayBlockingQueueTest extends BaseTest {
	
	/**
	 * 1亿
	 */
	private static final int MILLION = 100000000;

	@Test
	public void test() {
		Thread currentThread = Thread.currentThread();
		ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(MILLION);
		long time = System.currentTimeMillis();
		new Thread(()-> {
			long i = 0;
			while(i < MILLION) {
				try {
					queue.put(String.valueOf(i));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
		}).start();
		
		new Thread(()->{
			int i = 0;
			while(i < MILLION){
				try {
				    queue.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
			}
			p("测试完成，耗时："+((System.currentTimeMillis() - time) / 1000));
			LockSupport.unpark(currentThread);
		}).start();
		LockSupport.park();
	}
}
