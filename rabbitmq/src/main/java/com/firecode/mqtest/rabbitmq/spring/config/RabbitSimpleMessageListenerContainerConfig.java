package com.firecode.mqtest.rabbitmq.spring.config;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.MessagePostProcessor;

import com.rabbitmq.client.Channel;

import org.springframework.amqp.support.ConsumerTagStrategy;

/**
 * 消息消费监听器配置和消息接收到做预先处理
 * @author JIANG
 */
//@Configuration
@SuppressWarnings("unused")
public class RabbitSimpleMessageListenerContainerConfig {
	
	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer listener = new SimpleMessageListenerContainer(connectionFactory);
		// 设置要监听的队列（可以写多个）
		listener.setQueueNames("test.spring.queue.topic.bean");
		// 设置消费者数量
		listener.setConcurrentConsumers(1);
		// 设置最大消费者数量
		listener.setMaxConcurrentConsumers(10);
		// 消息确认失败是否重回队列
		listener.setDefaultRequeueRejected(false);
		// 消息确认模式（下面这个是自动确认） 
		listener.setAcknowledgeMode(AcknowledgeMode.AUTO);
	    // 监听器是否外露
		listener.setExposeListenerChannel(true);
		// 如果队列不存在是否自动声明
		//listener.setAutoDeclare(true);
		// 是否自动和Spring容器加载
		//listener.setAutoStartup(true);
		// bean名称
		//listener.setBeanName("");
		// 是否开启事务
		//listener.setChannelTransacted(false);
		/**
		 * 设置消费者唯一标签，可以在控制台：Channels > Consumers 处查看到。（用途：在生产环境使用这个，可以很明确的查看到消费者在消费那个队列）
		 */
		listener.setConsumerTagStrategy(new ConsumerTagStrategy(){
			@Override
			public String createConsumerTag(String queue) {
				
				return queue+"_"+UUID.randomUUID().toString().replaceAll("-", "");
			}
		});
		// 设置接收到消息后先做一些处理再消费
		listener.setAfterReceivePostProcessors(new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				System.err.println("接收到了消息，我先做了一些处理");
				return message;
			}
			
		});
		// 设置消息监听器
		listener.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				byte[] body = message.getBody();
				System.err.println("消费："+new String(body,StandardCharsets.UTF_8));
			}
		});
		return listener;
	}
	
}
