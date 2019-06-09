package com.firecode.mqtest.rabbitmq.spring;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.firecode.mqtest.rabbitmq.spring.config.RabbitAdminConfig;
import com.firecode.mqtest.rabbitmq.spring.config.RabbitTemplateConfig;

/**
 * 消息发送模板类简单使用测试（注意：测试前请将 RabbitAdminConfig 和 RabbitTemplateConfig 类上的 @Configuration 注解注释放开）
 * @author JIANG
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={RabbitAdminConfig.class,RabbitTemplateConfig.class})
public class RabbitTemplateConfigTest {
	
	@Autowired
	private RabbitTemplate template;
	
	/**
	 * 发送消息
	 */
	@Test
	public void sendMessage1(){
		MessageProperties props = new MessageProperties();
		props.getHeaders().put("name", "maomao");
		Message msg = new Message("消息对象".getBytes(StandardCharsets.UTF_8), props);
		/**
		 * exchange               交换机名称
		 * routingKey             路由键
		 * message                消息
		 */
		template.send("test.spring.exchange.topic.bean", "test.spring.queue.topic.bean", msg);
		
		/**
		 * exchange               交换机名称
		 * routingKey             路由键
		 * message                消息
		 */
		template.convertAndSend("test.spring.exchange.topic.bean", "test.spring.queue.topic.bean","消息字符串1");
		template.convertAndSend("test.spring.exchange.topic.bean", "test.spring.queue.topic.bean","消息字符串1");
		
	}
	
	/**
	 * 发送消息
	 */
	@Test
	public void sendMessage2(){
		MessageProperties props = new MessageProperties();
		//props.setContentType("text/plain");
		props.getHeaders().put("name", "maomao");
		Message msg = new Message("消息1".getBytes(StandardCharsets.UTF_8), props);
		/**
		 * exchange               交换机名称
		 * routingKey             路由键
		 * message                消息
		 * messagePostProcessor   消息转换器
		 */
		template.convertAndSend("test.spring.exchange.topic.bean", "test.spring.queue.topic.bean", msg, new MessagePostProcessor(){
			/**
			 * 消息转换处理器
			 */
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				System.err.println("消息经过转换处理");
				// 在消息头里面添加一些信息
				message.getMessageProperties().getHeaders().put("age", 30);
				return message;
			}
		});
	}

}
