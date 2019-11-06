package com.firecode.mqtest.activemq.helloworld;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
/**
 * 发布订阅
 * 一个发布者多个消费者，消费如果没有消费者 则直接丢弃
 * @author JIANG
 *
 */
public class Simple_003 {
	
	//配置 conf/activemq.xml
	private ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://127.0.0.1:61616");
	
	private class Producer {
		
		public void sendTextMessage(String content){
			Connection connection = null;
			Session session = null;
			MessageProducer producer = null;
			try{
				connection = factory.createConnection();
				connection.start();
				session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
				//主题目的地
				Destination destination = session.createTopic("test_topic");
				producer = session.createProducer(destination);
	            // 消息非持久化
				//producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				//消息体
				Message message = session.createTextMessage(content);
				//发送消息
				producer.send(message);
			}catch(Exception e) {
				e.printStackTrace();
			}finally{
				try{
					if(null != producer){
						producer.close();
					}
					if(null != session){
						session.close();
					}
					if(null != connection){
						connection.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private class Consumer {
		
		public void receiveTextMessage(){
			Connection connection = null;
			Session session = null;
			MessageConsumer consumer = null;
			try{
				connection = factory.createConnection();
				connection.start();
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Destination destination = session.createTopic("test_topic");
				consumer = session.createConsumer(destination);
				Message message = consumer.receive();
				System.err.println(((TextMessage)message).getText());
			}catch(Exception e) {
				e.printStackTrace();
			}finally{
				try{
					if(null != consumer){
						consumer.close();
					}
					if(null != session){
						session.close();
					}
					if(null != connection){
						connection.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private Producer createProducer(){
		
		return new Producer();
	}
	
	private Consumer createConsumer(){
		
		return new Consumer();
	}

	
	public static void main(String[] args) {
		Simple_003 simple = new Simple_003();
		simple.createProducer().sendTextMessage("测试测试");
		simple.createConsumer().receiveTextMessage();
	}
}
