package com.firecode.mqtest.activemq.jms;

import org.springframework.jms.annotation.JmsListener;
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
}
