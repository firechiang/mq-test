package com.firecode.mqtest.rabbitmq.feature.custom_consumers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * 自定义消费者
 * @author JIANG
 */
public class TestConsumer extends DefaultConsumer {

	public TestConsumer(Channel channel) {
		super(channel);
	}

	/**
	 * 自定义消费者逻辑
	 */
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
			throws IOException {
		System.err.println("消费：" + new String(body, StandardCharsets.UTF_8));
	}

}
