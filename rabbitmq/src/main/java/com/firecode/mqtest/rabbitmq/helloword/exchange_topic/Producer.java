package com.firecode.mqtest.rabbitmq.helloword.exchange_topic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * 
 * 所有发送到Topic类型的Exchange(交换机)的消息被转发到所有关心RoutingKey中指定Topic的Queue(队列)上，
   Exchange(交换机)将RoutingKey和某个Topic进行模糊匹配，此时队列需要绑定一个Topic（注意：# 模糊配置多个词，* 模糊配置一个词）
 * 生产者
 * @author JIANG
 */
public class Producer extends AbstractClient {
	
	
	@Test
	public void test() throws IOException {
		String routingKey1 = "test_topic.1";
		String routingKey2 = "test_topic.2";
		String routingKey3 = "test_topic.3.3";
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
		channel.basicPublish(exchangeName,routingKey1, null, "routingKey1的消息".getBytes(StandardCharsets.UTF_8));
		channel.basicPublish(exchangeName,routingKey2, null, "routingKey3的消息".getBytes(StandardCharsets.UTF_8));
		channel.basicPublish(exchangeName,routingKey3, null, "routingKey3的消息".getBytes(StandardCharsets.UTF_8));
	}

}
