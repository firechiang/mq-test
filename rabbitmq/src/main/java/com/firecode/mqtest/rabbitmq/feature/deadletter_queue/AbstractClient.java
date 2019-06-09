package com.firecode.mqtest.rabbitmq.feature.deadletter_queue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.firecode.mqtest.rabbitmq.common.AbstractAmqpClient;
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
 * 所有发送到 Direct类型的Exchange(交换机)的消息被转发到RoutingKey中指定的Queue(队列)上
 * @author JIANG
 */
public class AbstractClient extends AbstractAmqpClient {
	
	//交换机的名称
	protected String exchangeName = "test_exchange_dead_2";
	//交换机的类型
	protected String exchangeType = "topic";
	//队列的名称
	protected String queueName = "test_dead_queue_2";
	//路由的名称（只要是以test.dead开头的routingKey路由Key都会被路由到test_dead_queue_1这个队列）（注意：# 模糊配置多个词）
	protected String routingKey = "test.dead.#";
	
	// 死信队列交换机的名称
	protected String deadQueueExchange = "dlx.exchange.2";
	// 死信队列交换机的名称
	protected String deadQueueName = "dlx.queue.2";

	@Override
	public void before() throws IOException {
		Map<String, Object> arguments = new HashMap<>();
		// 设置死信消息要路由到那个Exchange（交换机）上
		arguments.put("x-dead-letter-exchange",deadQueueExchange);
		//声名(创建)一个交换机
		/**
		 * exchangeName  交换机名称
		 * exchangeType  交换机类型
		 * durable       是否持久化（注意：如果不持久化，服务重启将自动删除）
		 * autoDelete    当最后一个绑定到Exchange(交换机)上的队列删除后，自动删除该Exchange(交换机)
		 * internal      当前Exchange(交换机)是否用于RabbitMQ内部使用，默认为False
		 * arguments     扩展参数，用于扩展AMQP协议自定义一些属性
		 */
		channel.exchangeDeclare(exchangeName, exchangeType, true, false,null);
		// 声明(创建)队列（这个队列如果有死信要进入死信交换机）
		channel.queueDeclare(queueName, true, false, false, arguments);
		// 绑定队列到交换机再到路由
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//------------------------------死信相关配置--------------------------------------------//
		
		// 声明（创建）死信交换机
		channel.exchangeDeclare(deadQueueExchange, exchangeType, true, false,false, null);
		// 声明(创建)死信队列
		channel.queueDeclare(deadQueueName, true, false, false, null);
		// 绑定队列到交换机再到路由（# 号表示所有路由键都有机会被路由到死信队列）
		channel.queueBind(deadQueueName, deadQueueExchange, "#");
	}

}
