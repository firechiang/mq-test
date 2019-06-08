package com.firecode.mqtest.rabbitmq.helloword.exchange_fanout;

import java.io.IOException;

import com.firecode.mqtest.rabbitmq.common.AbstractAmqpClient;

/**
 * fanout类型的exchange(交换机)不处理任何RoutingKey(路由键)，只需要简单的将队列绑定到交换机,发送到交换机的消息都会被转发到与该交换机绑定的所有队列上，
 * 注意：fanout类型的Exchange(交换机)转发消息是最快的，性能是最好的
 * @author JIANG
 *
 */
public class AbstractClient extends AbstractAmqpClient {
	
	//交换机的名称
	protected String exchangeName = "test_fanout_exchange";
	//交换机的类型
	protected String exchangeType = "fanout";
	//队列的名称
	protected String queueName = "test_fanout_queue";
	// 不设置路由键，因为fanout类型的exchange(交换机)不会处理任何的路由键
	protected String routingKey = "";

	@Override
	public void before() throws IOException {
		//声名(创建)一个交换机
		/**
		 * exchangeName  交换机名称
		 * exchangeType  交换机类型
		 * durable       是否持久化
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
