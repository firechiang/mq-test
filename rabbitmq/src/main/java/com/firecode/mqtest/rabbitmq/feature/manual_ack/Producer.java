package com.firecode.mqtest.rabbitmq.feature.manual_ack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.rabbitmq.client.AMQP;

/**
 * 
 * 手动确认消息和是否重回队列
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
