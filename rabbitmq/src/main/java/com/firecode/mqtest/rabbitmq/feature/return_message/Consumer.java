package com.firecode.mqtest.rabbitmq.feature.return_message;

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
 * Return Listener 用于处理一些不可路由的消息，就是发送失败的消息 消费者
 * 
 * @author JIANG
 */
public class Consumer extends AbstractClient {

	@Test
	public void test() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		// 创建消费者
		DefaultConsumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				System.err.println("消费：" + new String(body, StandardCharsets.UTF_8));
			}
		};
		// 设置Channel
		/**
		 * queue 队列的名称 autoAck 是否自动消息确认 consumer 消费者
		 */
		channel.basicConsume(queueName, true, consumer);
		TimeUnit.DAYS.sleep(1L);
	}
}
