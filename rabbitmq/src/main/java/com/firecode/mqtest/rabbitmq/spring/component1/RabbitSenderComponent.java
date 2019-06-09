package com.firecode.mqtest.rabbitmq.spring.component1;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 简单的消息发送组件
 * @author JIANG
 */
@Component
@EnableAutoConfiguration
public class RabbitSenderComponent {
	
	@Autowired
	private RabbitTemplate template;
	
	/**
	 * 监听消息是否投递成功
	 */
	ConfirmCallback confirmCallback = new ConfirmCallback() {
		
		@Override
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
			if(ack){
				System.err.println("消息ID："+correlationData+"，投递成功");
			}else{
				System.err.println("消息投递失败");
			}
		}
	};
	
	/**
	 * 监听不可路由的消息，就是发送失败的消息
	 */
	ReturnCallback returnCallback = new ReturnCallback() {

		@Override
		public void returnedMessage(org.springframework.amqp.core.Message message, int replyCode, String replyText,
				String exchange, String routingKey) {
			System.err.println(new String(message.getBody(),StandardCharsets.UTF_8)+"，发送失败，原因：不可路由");
		}
	};
	
	
	public void send(Object message,Map<String,Object> props) throws Exception {
		// 设置监听消息是否投递成功
		template.setConfirmCallback(confirmCallback);
		// 设置监听不可路由的消息，就是发送失败的消息
		template.setReturnCallback(returnCallback);
		MessageHeaders header = new MessageHeaders(props);
		Message<Object> msg = MessageBuilder.createMessage(message, header);
		/**
		 * exchange               交换机名称
		 * routingKey             路由键
		 * message                消息
		 * correlationData        消息唯一ID
		 */
		template.convertAndSend("test.spring.exchange.topic.bean", "test.spring.queue.topic.bean", msg,new CorrelationData(UUID.randomUUID().toString().replaceAll("-", "")));
	}

}
