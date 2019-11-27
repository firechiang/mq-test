package com.firecode.mqtest.rocketmq.spring.simple;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = ProducerTestQueue.QUEUE_NAME, selectorExpression = "*", consumerGroup = ProducerTestQueue.QUEUE_NAME_GROUP)
public class ConsumerTestQueue implements RocketMQListener<MessageExt> {

	@Override
	public void onMessage(MessageExt message) {
		System.err.println("消费者消费消息了，消息ID："+message.getMsgId()+"，消息内容："+new String(message.getBody()));
	}

}
