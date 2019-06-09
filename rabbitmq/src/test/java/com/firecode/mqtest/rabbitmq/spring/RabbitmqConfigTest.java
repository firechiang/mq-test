package com.firecode.mqtest.rabbitmq.spring;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.firecode.mqtest.rabbitmq.BasicTest;
import com.firecode.mqtest.rabbitmq.spring.config.RabbitmqConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={RabbitmqConfig.class})
public class RabbitmqConfigTest extends BasicTest {
	
	@Autowired
	private RabbitAdmin admin;
	
	/**
	 * 创建交换机
	 */
	@Test
	public void exchangeTest(){
		//所有发送到 Direct类型的Exchange(交换机)的消息被转发到RoutingKey中指定的Queue(队列)上
		admin.declareExchange(new DirectExchange("test.spring.direct", false, false));
		//所有发送到 Topic类型的Exchange(交换机)的消息被转发到所有关心RoutingKey中指定Topic的Queue(队列)上。
		//Exchange(交换机)将RoutingKey和某个Topic进行模糊匹配，此时队列需要绑定一个Topic（注意：# 模糊配置多个词，* 模糊配置一个词）
		admin.declareExchange(new TopicExchange("test.spring.topic", false, false));
		//不处理任何RoutingKey(路由键)，只需要简单的将队列绑定到交换机,发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。
		//注意：fanout类型的Exchange(交换机)转发消息是最快的，性能是最好的
		admin.declareExchange(new FanoutExchange("test.spring.fanout", false, false));
	}
	
	/**
	 * 创建队列
	 */
	@Test
	public void queueTest(){
		admin.declareQueue(new Queue("test.spring.direct.queue",false));
		admin.declareQueue(new Queue("test.spring.topic.queue",false));
		admin.declareQueue(new Queue("test.spring.fanout.queue",false));
	}
	
	/**
	 * 绑定队列到交换机
	 */
	@Test
	public void bindingTest(){
		// 基本绑定方法
		admin.declareBinding(new Binding("test.spring.direct.queue",       // 队列的名称
				                         Binding.DestinationType.QUEUE,    // 绑定类型
				                         "test.spring.direct",             // 交换机名称
				                         "test.spring.direct.routing",     // 路由键
				                         new HashMap<>()));                // 附加属性
		
		// 边绑定边创建
		admin.declareBinding(BindingBuilder.bind(
				                           new Queue("test.spring.topic.queue",false))  // 队列，不存在会自动创建
				                           .to(new TopicExchange("test.spring.topic",false,false))// 交换机，不存在会自动创建
				                           .with("test.spring.topic.*"));// 路由键
	}
	
	/**
	 * 清空队列里面的消息
	 */
	@Test
	public void purgeQueueTest(){
		/**
		 * queueName 队列名称
		 * noWait    是否需要等待
		 */
		admin.purgeQueue("test.spring.topic.queue", false);
	}
	
}
