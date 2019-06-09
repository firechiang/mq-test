package com.firecode.mqtest.rabbitmq.spring.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
	
	@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setAddresses("192.168.229.133");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");
		return factory;
	}
	
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
		RabbitAdmin admin = new RabbitAdmin(connectionFactory);
		// 自动加载RabbitMQ相关信息，否则 RabbitAdmin 基本无法使用
		admin.setAutoStartup(true);
		return admin;
	}
}
