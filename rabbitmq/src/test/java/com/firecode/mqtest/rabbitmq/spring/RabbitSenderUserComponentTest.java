package com.firecode.mqtest.rabbitmq.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.firecode.mqtest.rabbitmq.domain.User;
import com.firecode.mqtest.rabbitmq.spring.component2.RabbitSenderUserComponent;
import com.firecode.mqtest.rabbitmq.spring.config.RabbitTemplateConfig;

/**
 * User消息发送组件测试（注意：测试前要先启动 Application 类，再将 RabbitAdminConfig 类上的 @Configuration 注解注释掉）
 * @author JIANG
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={RabbitTemplateConfig.class,RabbitSenderUserComponent.class})
public class RabbitSenderUserComponentTest {
	
	@Autowired
	private RabbitSenderUserComponent rabbitSenderUserComponent;
	
	@Test
	public void send() throws Exception {
		Map<String,Object> props = new HashMap<>();
		User u = new User(System.currentTimeMillis(),String.valueOf(new Random().nextInt(100)));
		rabbitSenderUserComponent.send(u, props);
	}

}
