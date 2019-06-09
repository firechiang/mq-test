package com.firecode.mqtest.rabbitmq.spring_cloud_stream;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firecode.mqtest.rabbitmq.spring_cloud_stream.producer.RabbitmqStreamSender;

/**
 * 测试 Spring-Cloud-Stream 消息发送
 * @author JIANG
 */
@RestController
public class TestController {
	
	@Autowired
	private RabbitmqStreamSender rabbitmqStreamSender;
	
	
	@GetMapping("/send")
	public boolean send() throws Exception{
		long time = System.currentTimeMillis();
		Map<String,Object> properties = new HashMap<>();
		properties.put("name", "maomao");
		rabbitmqStreamSender.sendMessage("消息"+time, properties);
		return true;
	}
	
}
