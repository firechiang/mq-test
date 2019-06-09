package com.firecode.mqtest.rabbitmq.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
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
	public void adminTest(){
		//所有发送到 Direct类型的Exchange(交换机)的消息被转发到RoutingKey中指定的Queue(队列)上
		admin.declareExchange(new DirectExchange("test.spring.direct", false, false));
		//所有发送到 Topic类型的Exchange(交换机)的消息被转发到所有关心RoutingKey中指定Topic的Queue(队列)上。
		//Exchange(交换机)将RoutingKey和某个Topic进行模糊匹配，此时队列需要绑定一个Topic（注意：# 模糊配置多个词，* 模糊配置一个词）
		admin.declareExchange(new TopicExchange("test.spring.topic", false, false));
		//不处理任何RoutingKey(路由键)，只需要简单的将队列绑定到交换机,发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。
		//注意：fanout类型的Exchange(交换机)转发消息是最快的，性能是最好的
		admin.declareExchange(new FanoutExchange("test.spring.fanout", false, false));
	}
}
