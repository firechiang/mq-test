package com.firecode.mqtest.rocketmq.spring.simple;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firecode.mqtest.rocketmq.domain.MsgContent;

@RestController
public class ProducerTestQueue {
	
	@Autowired
    private RocketMQTemplate rocketMQTemplate;
	
	private String[] names = {"maomoa","tianti","jiang","kajdiaj","zssdfsfs"};
	
	public static final String QUEUE_NAME = "test_queue";
	
	public static final String QUEUE_NAME_GROUP = "test_queue_group";
	
	@GetMapping("/senMsg")
	public SendResult sendMessage(@RequestParam(name="desc",required = false) String desc) throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		MsgContent msgContent = new MsgContent(names[random.nextInt(5)], System.currentTimeMillis(),desc);
		SendResult syncSend = rocketMQTemplate.syncSend(QUEUE_NAME, MessageBuilder.withPayload(msgContent).build());
		return syncSend;
	}
	
}
