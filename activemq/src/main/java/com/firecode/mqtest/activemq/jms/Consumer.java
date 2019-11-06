package com.firecode.mqtest.activemq.jms;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
	
    @JmsListener(destination = "simple.topic",containerFactory="jmsListenerContainerTopic")
    public void receiveTopic(String text){
        System.out.println("Topic Consumer1:"+text);
    }
    
    @JmsListener(destination = "simple.topic",containerFactory="jmsListenerContainerTopic")
    public void receiveTopic2(String text){
        System.out.println("Topic Consumer2:"+text);
    }
    
    @JmsListener(destination = "simple.queue",containerFactory="jmsListenerContainerQueue")
    public void reviceQueue(String text){
        System.out.println("Queue Consumer:"+text);
    }
    
    /**
     * @SendTo注解将方法的返回值，重新放入其它队列，公共消费
     * @param text
     * @return
     */
    @JmsListener(destination = "simple.queue1",containerFactory="jmsListenerContainerQueue")
    @SendTo("simple.queue2")
    public String reviceQueue1(String text){
        System.out.println("Queue Consumer:"+text);
        return "我收到消息了";
    }
    
    @JmsListener(destination = "simple.queue2",containerFactory="jmsListenerContainerQueue")
    public void reviceQueue2(TextMessage msg) throws JMSException {
    	// 处理逻辑
    	// 手动确认消息
    	msg.acknowledge();
    }
    
    
    
    
}
