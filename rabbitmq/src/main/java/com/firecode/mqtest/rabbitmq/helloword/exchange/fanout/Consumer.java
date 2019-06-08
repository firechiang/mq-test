package com.firecode.mqtest.rabbitmq.helloword.exchange.fanout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 
 * fanout类型的exchange(交换机)不处理任何RoutingKey(路由键)，只需要简单的将队列绑定到交换机,发送到交换机的消息都会被转发到与该交换机绑定的所有队列上，
 * 注意：fanout类型的Exchange(交换机)转发消息是最快的，性能是最好的
 * 消费者
 * @author JIANG
 */
public class Consumer extends AbstractClient {
	
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
			// 获取消息（注意：如果没有消息，这个会一直阻塞（还有一个带参数的方法，可以设置等待超时时间））
			Delivery nextDelivery = consumer.nextDelivery();
			byte[] body = nextDelivery.getBody();
			System.err.println("消费："+new String(body,StandardCharsets.UTF_8));
		}
	}
}
