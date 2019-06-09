package com.firecode.mqtest.rabbitmq.spring;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.firecode.mqtest.rabbitmq.spring.component1.RabbitSenderComponent;
import com.firecode.mqtest.rabbitmq.spring.config.RabbitTemplateConfig;

/**
 * 消息发送组件测试（注意：测试前请将 RabbitAdminConfig 类上的 @Configuration 注解注释掉）
 * @author JIANG
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={RabbitTemplateConfig.class,RabbitSenderComponent.class})
public class RabbitSenderComponentTest {
	
	@Autowired
	private RabbitSenderComponent rabbitSenderComponent;
	
	@Test
	public void send() throws Exception {
		Map<String,Object> props = new HashMap<>();
		rabbitSenderComponent.send("消息："+System.currentTimeMillis(), props);
	}

}
