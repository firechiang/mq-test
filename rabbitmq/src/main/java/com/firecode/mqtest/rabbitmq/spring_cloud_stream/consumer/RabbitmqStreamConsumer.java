package com.firecode.mqtest.rabbitmq.spring_cloud_stream.consumer;

import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

//启用绑定
@EnableBinding(Barista.class)
@Service
public class RabbitmqStreamConsumer {  

    @StreamListener(Barista.INPUT_CHANNEL)  
    public void receiver(Message<Object> message) throws Exception {  
		Channel channel = (com.rabbitmq.client.Channel) message.getHeaders().get(AmqpHeaders.CHANNEL);
		Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
    	System.out.println("Input Stream 1 接受数据：" + message);
		/**
		 * 手动指定消息确认成功
		 * deliveryTag    消息唯一标识
		 * multiple       是否支持批量确认
		 */
    	channel.basicAck(deliveryTag, false);
    }  
}  
