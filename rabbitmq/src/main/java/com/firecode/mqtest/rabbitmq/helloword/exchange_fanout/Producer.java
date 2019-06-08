package com.firecode.mqtest.rabbitmq.helloword.exchange_fanout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

/**
 * fanout类型的exchange(交换机)不处理任何RoutingKey(路由键)，只需要简单的将队列绑定到交换机,发送到交换机的消息都会被转发到与该交换机绑定的所有队列上，
 * 注意：fanout类型的Exchange(交换机)转发消息是最快的，性能是最好的
 * 生产者
 * @author JIANG
 */
public class Producer extends AbstractClient {
	
	@Test
	public void test() throws IOException {
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
		channel.basicPublish(exchangeName,"", null, "消息1".getBytes(StandardCharsets.UTF_8));
		channel.basicPublish(exchangeName,"", null, "消息2".getBytes(StandardCharsets.UTF_8));
		channel.basicPublish(exchangeName,"", null, "消息3".getBytes(StandardCharsets.UTF_8));
	}

}
