package com.firecode.mqtest.rabbitmq.feature.custom_consumers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 自定义消费者
 * 消费者
 * @author JIANG
 */
public class Consumer extends AbstractClient {
	
	@Test
	public void test() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		// 创建消费者
		TestConsumer testConsumer = new TestConsumer(channel);
		// 设置Channel
		/**
		 * queue        队列的名称
		 * autoAck      是否自动消息确认
		 * consumer     消费者
		 */
		channel.basicConsume(queueName, true,testConsumer);
		TimeUnit.DAYS.sleep(1L);
	}
}
