package com.firecode.mqtest.rabbitmq.helloword.exchange.topic;

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
 * 
 * 所有发送到 Topic类型的Exchange(交换机)的消息被转发到所有关心RoutingKey中指定Topic的Queue(队列)上，
   Exchange(交换机)将RoutingKey和某个Topic进行模糊匹配，此时队列需要绑定一个Topic（注意：# 模糊配置多个词，* 模糊配置一个词）
 * 消费者
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
		 * queue        队列的名称
		 * autoAck      是否自动消息确认
		 * consumer     消费者
		 */
		channel.basicConsume(queueName, true,consumer);
		TimeUnit.DAYS.sleep(1L);
	}
}
