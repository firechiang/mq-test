package com.firecode.mqtest.rabbitmq.helloword.message.props;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.firecode.mqtest.rabbitmq.common.AbstractAmqpClient;
import com.rabbitmq.client.AMQP;

/**
 * 发送自带属性的消息
 * 生产者
 * @author JIANG
 */
public class Producer extends AbstractAmqpClient {
	
	
	@Test
	public void test() throws IOException {
		// 自定义一些属性
		Map<String,Object> headers = new HashMap<>(16);
		headers.put("testName", "maomao");
		AMQP.BasicProperties proper = new AMQP.BasicProperties()
				                              .builder()
				                              .deliveryMode(2)      // 1 非持久化消息，2 持久化消息
				                              .contentType(StandardCharsets.UTF_8.name())
				                              .expiration("5000")  // 消息5秒过期
				                              .headers(headers)    // 自定义一些属性
				                              .build();
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
			channel.basicPublish("",queueName, proper, (msg+i).getBytes(StandardCharsets.UTF_8));
		}
	}

}
