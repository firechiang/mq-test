package com.firecode.mqtest.rabbitmq.feature.deadletter_queue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.rabbitmq.client.AMQP;

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
 * 生产者
 * @author JIANG
 */
public class Producer extends AbstractClient {
	
	
	@Test
	public void test() throws IOException, InterruptedException {
		String msg = "消息";
		int size = 10;
		for(int i=0;i<size;i++) {
			Map<String,Object> headers = new HashMap<>();
			headers.put("count", i);
			AMQP.BasicProperties proper = new AMQP.BasicProperties
					                              .Builder()
					                              .deliveryMode(2) // 1 非持久化消息，2 持久化消息
					                              .contentType(StandardCharsets.UTF_8.name())
					                              .headers(headers)
					                              .build();
			/**
			 * exchange     交换机名称（注意：如果没有指定交换机名称，默认使用 (AMQP default)交换机）
			 * 
			 * routingKey   路由的名称（注意：如果没有指定交换机名称，默认使用 (AMQP default)交换机，
			 *              它会直接将"路由的名称"当队列的名称来用，也就是直接将消息发送到这个"路由的名称"上，前提是这个队列名称已存在）
			 *              
			 * props        消息的一些附加属性
			 * 
			 * body         消息数据
			 */
			channel.basicPublish(exchangeName,routingKey, proper, (msg+i).getBytes(StandardCharsets.UTF_8));
		}
	}
}
