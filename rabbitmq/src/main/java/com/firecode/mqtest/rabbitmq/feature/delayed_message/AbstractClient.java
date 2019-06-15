package com.firecode.mqtest.rabbitmq.feature.delayed_message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.firecode.mqtest.rabbitmq.common.AbstractAmqpClient;
/**
 * 延迟交换机，它会将消息延迟推送到队列
 * 所有发送到 Direct类型的Exchange(交换机)的消息被转发到RoutingKey中指定的Queue(队列)上
 * @author JIANG
 */
public class AbstractClient extends AbstractAmqpClient {
	
	//交换机的名称
	protected String exchangeName = "test_exchange_delayed";
	//交换机的类型
	protected String exchangeType = "x-delayed-message";
	//队列的名称
	protected String queueName = "test_delayed_queue";
	//路由的名称
	protected String routingKey = "test.delayed.routing";

	@Override
	public void before() throws IOException {
		Map<String, Object> arguments = new HashMap<>();
		// 指定实际的交换机类型（注意：这个是必须参数）
		arguments.put("x-delayed-type", "direct");
		//声名(创建)一个交换机
		/**
		 * exchangeName  交换机名称
		 * exchangeType  交换机类型
		 * durable       是否持久化（注意：如果不持久化，服务重启将自动删除）
		 * autoDelete    当最后一个绑定到Exchange(交换机)上的队列删除后，自动删除该Exchange(交换机)
		 * internal      当前Exchange(交换机)是否用于RabbitMQ内部使用，默认为False
		 * arguments     扩展参数，用于扩展AMQP协议自定义一些属性
		 */
		channel.exchangeDeclare(exchangeName, exchangeType, true, false,false, arguments);
		// 声明(创建)队列
		channel.queueDeclare(queueName, false, false, false, null);
		// 绑定队列到交换机再到路由
		channel.queueBind(queueName, exchangeName, routingKey);
	}

}
