package com.firecode.mqtest.rabbitmq.spring_cloud_stream.producer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

// 启用绑定
@EnableBinding(Barista.class)
@Service  
public class RabbitmqStreamSender {  
  
    @Autowired  
    private Barista barista;  
    
    // 发送消息
    public String sendMessage(Object message, Map<String, Object> properties) throws Exception {  
        try{
        	MessageHeaders mhs = new MessageHeaders(properties);
        	Message<Object> msg = MessageBuilder.createMessage(message, mhs);
            boolean sendStatus = barista.logoutput().send(msg);
            System.out.println("发送数据：" + message + ",sendStatus: " + sendStatus);
        }catch (Exception e){  
        	e.printStackTrace();
            throw new RuntimeException(e.getMessage());
           
        }  
        return null;
    }  
    
}  