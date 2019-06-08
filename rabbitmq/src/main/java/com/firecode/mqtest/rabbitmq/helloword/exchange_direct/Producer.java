package com.firecode.mqtest.rabbitmq.helloword.exchange_direct;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * 
 * 所有发送到 Direct类型的Exchange(交换机)的消息被转发到RoutingKey中指定的Queue(队列)上
 * 生产者
 * @author JIANG
 */
public class Producer extends AbstractClient {
	
	
	@Test
	public void test() throws IOException {
		String msg = "消息";
		int size = 10;
		for(int i=0;i<size;i++) {
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
			channel.basicPublish(exchangeName,routingKey, null, (msg+i).getBytes(StandardCharsets.UTF_8));
		}
	}

}
