package com.firecode.mqtest.rabbitmq.spring.component1;

import java.io.IOException;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

/**
 * 简单的消息监听组件
 * @author JIANG
 */
@Component
public class RabbitConsumerComponent {
	
	// 关系建立和绑定注解（注意：这个注解如果交换机或队列不存在它会自动创建和绑定）
	@RabbitListener(bindings=@QueueBinding(
	    value=@Queue(value="test.spring.queue.topic.bean",durable="false"),
	    exchange=@Exchange(value="test.spring.exchange.topic.bean",type=ExchangeTypes.TOPIC,durable="false",ignoreDeclarationExceptions="true"),
	    key="test.spring.queue.topic.*"
	))
	// 消费者处理器标识注解
	@RabbitHandler
	public void onMessage(Message<Object> message,Channel channel) throws IOException{
		Object payload = message.getPayload();
		System.err.println("消费者："+payload);
		Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
		/**
		 * 手动指定消息确认成功
		 * deliveryTag    消息唯一标识
		 * multiple       是否支持批量确认
		 */
		channel.basicAck(deliveryTag, false);
	}
}
