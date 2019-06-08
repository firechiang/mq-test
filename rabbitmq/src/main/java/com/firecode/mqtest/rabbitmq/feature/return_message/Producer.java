package com.firecode.mqtest.rabbitmq.feature.return_message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ReturnListener;

/**
 * Return Listener 用于处理一些不可路由的消息，就是发送失败的消息
 * 生产者
 * @author JIANG
 */
public class Producer extends AbstractClient {
	
	
	@Test
	public void test() throws IOException, InterruptedException {
		/**
		 * 监听不可路由的消息，就是发送失败的消息
		 */
		channel.addReturnListener(new ReturnListener(){

			@Override
			public void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
					BasicProperties properties, byte[] body) throws IOException {
				
				System.err.println(new String(body,StandardCharsets.UTF_8)+"，发送失败，原因：不可路由");
				
			}
		});
		/**
		 * exchange     交换机名称（注意：如果没有指定交换机名称，默认使用 (AMQP default)交换机）
		 * 
		 * routingKey   路由的名称（注意：如果没有指定交换机名称，默认使用 (AMQP default)交换机，
		 *              它会直接将"路由的名称"当队列的名称来用，也就是直接将消息发送到这个"路由的名称"上，前提是这个队列名称已存在）
		 *              
		 * mandatory    如果为true，监听器则接收路由不可达的消息，如果为false Broker端自动删除该消息，监听器接收不到
		 *              
		 * props        消息的一些附加属性
		 * 
		 * body         消息数据
		 */
		// 可配置，可发送成功
		channel.basicPublish(exchangeName,"test.return.1", true,null, "消息1".getBytes(StandardCharsets.UTF_8));
		// 不可配置，会发送失败
		channel.basicPublish(exchangeName,"test.return.1.2", true,null, "消息2".getBytes(StandardCharsets.UTF_8));
		TimeUnit.DAYS.sleep(1L);
	}
}
