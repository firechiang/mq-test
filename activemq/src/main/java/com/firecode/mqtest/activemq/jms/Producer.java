package com.firecode.mqtest.activemq.jms;

import javax.jms.Queue;
import javax.jms.Topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class Producer {

	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;
	
    @Autowired
    private Queue queue;
    @Autowired
    private Topic topic;

	private static int count = 0;

	@Scheduled(fixedDelay = 3000)
	public void send() {
		this.jmsMessagingTemplate.convertAndSend(queue, "hi.activeMQ,index=" + count);
		this.jmsMessagingTemplate.convertAndSend(topic, "hi,activeMQ( topic )，index=" + count++);
	}

}
