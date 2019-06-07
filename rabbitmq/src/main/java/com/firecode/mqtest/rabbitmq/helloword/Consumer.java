package com.firecode.mqtest.rabbitmq.helloword;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.firecode.mqtest.rabbitmq.common.AbstractAmqpClient;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 消费者
 * @author JIANG
 */
public class Consumer extends AbstractAmqpClient {
	
	@Test
	public void test() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		// 创建消费者
		QueueingConsumer consumer = new QueueingConsumer(channel);
		// 设置Channel
		/**
		 * queue        队列的名称
		 * autoAck      是否自动消息确认
		 * consumer     消费者
		 */
		channel.basicConsume(queueName, true,consumer);
		
		while(true){
			// 获取消息（注意：如果没有消息，这个会一直阻塞（有个参数可以设置等待时间，在超时））
			Delivery nextDelivery = consumer.nextDelivery();
			byte[] body = nextDelivery.getBody();
			System.err.println("消费："+new String(body,StandardCharsets.UTF_8));
		}
	}
}
