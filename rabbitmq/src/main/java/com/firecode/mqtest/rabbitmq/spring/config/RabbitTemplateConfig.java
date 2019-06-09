package com.firecode.mqtest.rabbitmq.spring.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息发送模板类使用配置
 * @author JIANG
 */
@Configuration
public class RabbitTemplateConfig {
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		return template;
	}
	
	/**
	 * 声明（创建）交换机，交换机的种类：FanoutExchange，DirectExchange，TopicExchange
	 * @return
	 */
	@Bean
	public TopicExchange topicExchange(){
		//return new FanoutExchange("test.spring.exchange.fanout.bean", false, false);
	    //return new DirectExchange("test.spring.exchange.direct.bean", false, false);
		return new TopicExchange("test.spring.exchange.topic.bean", false, false);
	}
	
	/**
	 * 创建（声明）队列
	 * @return
	 */
	@Bean
	public Queue queue(){
		
		return new Queue("test.spring.queue.topic.bean",false);
	}
	
	/**
	 * 将队列绑定到交换机
	 * @return
	 */
	@Bean
	public Binding binding(){
		
		return BindingBuilder.bind(queue()).to(topicExchange()).with("test.spring.queue.topic.*");
	}
	

}
