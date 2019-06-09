package com.firecode.mqtest.rabbitmq.helloword.exchange.topic;

import java.io.IOException;

import com.firecode.mqtest.rabbitmq.common.AbstractAmqpClient;

/**
 * 所有发送到 Topic类型的Exchange(交换机)的消息被转发到所有关心RoutingKey中指定Topic的Queue(队列)上，
   Exchange(交换机)将RoutingKey和某个Topic进行模糊匹配，此时队列需要绑定一个Topic（注意：# 模糊配置多个词，* 模糊配置一个词）
 * @author JIANG
 *
 */
public class AbstractClient extends AbstractAmqpClient {
	
	//交换机的名称
	protected String exchangeName = "test_topic_exchange";
	//交换机的类型
	protected String exchangeType = "topic";
	//队列的名称
	protected String queueName = "test_topic_queue";
	//路由的名称（只要是以test_topic开头的routingKey都会被路由到test_topic_queue这个队列）（注意：* 模糊配置一个词，test_topi.rere 可配置，test_topi.rere.asas 不可配置）
	//protected String routingKey = "test_topic.*";
	//路由的名称（只要是以test_topic开头的routingKey都会被路由到test_topic_queue这个队列）（注意：# 模糊配置多个词）
	protected String routingKey = "test_topic.#";

	@Override
	public void before() throws IOException {
		//声名(创建)一个交换机
		/**
		 * exchangeName  交换机名称
		 * exchangeType  交换机类型
		 * durable       是否持久化（注意：如果不持久化，服务重启将自动删除）
		 * autoDelete    当最后一个绑定到Exchange(交换机)上的队列删除后，自动删除该Exchange(交换机)
		 * internal      当前Exchange(交换机)是否用于RabbitMQ内部使用，默认为False
		 * arguments     扩展参数，用于扩展AMQP协议自定义一些属性
		 */
		channel.exchangeDeclare(exchangeName, exchangeType, true, false,false, null);
		// 声明(创建)队列
		channel.queueDeclare(queueName, false, false, false, null);
		// 绑定队列到交换机再到路由
		channel.queueBind(queueName, exchangeName, routingKey);
	}

}
