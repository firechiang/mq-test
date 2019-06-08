package com.firecode.mqtest.rabbitmq.feature.deadletter_queue;

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
 * 死信队列（没有任何消费者消费它），利用DLX，当消息在一个队列中变成死信（dead message）之后，
 * 它能被从新publish（发送）到另一个Exchange（交换机），这个Exchange（交换机）就是DLX。
 * 
 * DXL也是一个正常的Exchange（交换机），和一般的Exchange（交换机）没有区别，它能在任何的队列上被指定，
 * 实际上就是设置队列的属性。
 * 
 * 
 * 消息比变成死信的几种情况：
 * 1，当消息被确认消费失败，且没有重回队列（示列代码：channel.basicNack(envelope.getDeliveryTag(), true, false)）
 * 2，消息TTL过期，消息过期了但没有被消费
 * 3，队列达到最大长度
 * 
 * 
 * 当某个队列中有死信时，且设置了死信队列，RabbitMQ就会自动的将这个消息重新发送到设置的Exchange（交换机）上去，进而被路由到另一个队列。
 * 
 * 消费者（执行完成以后看看死信队列是否有一条消息进入）
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
				// 如果是第0个我们设置消息确认失败
				if((Integer)properties.getHeaders().get("count") == 0){
					/**
					 * 手动指定消息确认失败（这条消息将会进入死信队列）
					 * deliveryTag    消息唯一标识
					 * multiple       是否支持批量确认
					 * requeue        是否重回队列（注意：如果为true重回队列，这条消息会一直推一直推，变成死循环，直到确认消费成功为止；如果为false，这条消息就算消费了）
					 */
					channel.basicNack(envelope.getDeliveryTag(), true, false);
				}else {
					System.err.println("消费：" + new String(body, StandardCharsets.UTF_8));
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
