package com.firecode.mqtest.rabbitmq.feature.consumers_limiting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 消费端限流
 * RabbitMQ提供了一种QOS（服务质量保证）功能，就是在非自动确认消息的前提下
 * 如果一定数目的消息，未被确认前，不进行消费新的消息。
 * 实现原理：通过基于Consumer或者Channels设置QOS的值
 * 
 * 消费者
 * @author JIANG
 */
public class Consumer extends AbstractClient {
	
	@Test
	public void test() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		/**
		 * 限流设置
		 * prefetchSize   单条消息的大小限制（设置为0表示不限制）
		 * prefetchCount  RabbitMQ最多推送多少条消息给消费者，并且全部确认以后，才会再给消费者推送消息（推荐设置为1）（注意：这个设置是在非自动确认消息的前提下生效）
		 * global         false表示在Consumer级别限流，true 表示在Channel级别限流
		 */
		channel.basicQos(0, 1, false);
		// 创建消费者
		DefaultConsumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				System.err.println("消费：" + new String(body, StandardCharsets.UTF_8));
				
				/**
				 * 手动确认消息
				 * deliveryTag 消息唯一标识
				 * multiple    是否支持批量确认(如果是一条一条推送（prefetchCount=1）就设置为false，如果多条推送（prefetchCount>1）就设置为reue)
				 */
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		/**
		 * queue        队列的名称
		 * autoAck      是否自动消息确认（注意：要实现限流功能，这个设置一定要是false）
		 * consumer     消费者
		 */
		channel.basicConsume(queueName, false,consumer);
		TimeUnit.DAYS.sleep(1L);
	}
}
