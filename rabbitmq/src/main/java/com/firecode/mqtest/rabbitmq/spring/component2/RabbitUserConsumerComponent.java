package com.firecode.mqtest.rabbitmq.spring.component2;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.firecode.mqtest.rabbitmq.domain.User;
import com.rabbitmq.client.Channel;

/**
 * User消息监听组件
 * @author JIANG
 */
@Component
public class RabbitUserConsumerComponent {
	
	// 关系建立和绑定注解（注意：这个注解如果交换机或队列不存在它会自动创建和绑定）
	@RabbitListener(bindings=@QueueBinding(
	    value=@Queue(value="${spring.rabbitmq.listener.user.queue.name}",durable="${spring.rabbitmq.listener.user.queue.durable}"),
	    exchange=@Exchange(value="${spring.rabbitmq.listener.user.exchange.name}",type="${spring.rabbitmq.listener.user.exchange.type}",durable="${spring.rabbitmq.listener.user.exchange.durable}",ignoreDeclarationExceptions="${spring.rabbitmq.listener.user.exchange.ignoreDeclarationExceptions}"),
	    key="${spring.rabbitmq.listener.user.key}"
	))
	// 消费者处理器标识注解
	@RabbitHandler
	public void onMessage(@Payload User user,@Headers Map<String,Object> headers,Channel channel) throws IOException{
		System.err.println("消费者："+user);
		Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
		/**
		 * 手动指定消息确认成功
		 * deliveryTag    消息唯一标识
		 * multiple       是否支持批量确认
		 */
		channel.basicAck(deliveryTag, false);
	}
}
