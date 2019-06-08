package com.firecode.mqtest.rabbitmq.feature.manual_ack;

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
 * 手动确认消息和是否重回队列
 * 
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
				// 如果是第0个我们设置消息确认失败
				if((Integer)properties.getHeaders().get("count") == 0){
					/**
					 * 手动指定消息确认失败
					 * deliveryTag    消息唯一标识
					 * multiple       是否支持批量确认
					 * requeue        是否重回队列（注意：如果为true重回队列，这条消息会一直推一直推，变成死循环，直到确认消费成功为止；如果为false，这条消息就算消费了）
					 */
					channel.basicNack(envelope.getDeliveryTag(), true, false);
				}else {
					/**
					 * 手动指定消息确认成功
					 * deliveryTag    消息唯一标识
					 * multiple       是否支持批量确认
					 */
					channel.basicAck(envelope.getDeliveryTag(), true);
				}
			}
		};
		/**
		 * queue        队列的名称
		 * autoAck      是否自动消息确认
		 * consumer     消费者
		 */
		channel.basicConsume(queueName, false,consumer);
		TimeUnit.DAYS.sleep(1L);
	}
}
