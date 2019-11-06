package com.firecode.mqtest.activemq.helloworld;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
/**
 * 事物消息
 * 触发消息重发情况
 * 1，在使用事物的Session中，调用session.rollback()函数
 * 2，在使用事物的Session中，调用session.commit()函数之前断开连接了或关闭Session了
 * 3，在Session中使用Session.CLIENT_ACKNOWLEDGE手动签收模式，并且调用了session.recover()函数
 * 4，自动应答失败
 * @author JIANG
 */
public class Simple_004 {
	
	//配置 conf/activemq.xml
	private final ConnectionFactory factory;
	
	public Simple_004() {
		factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://127.0.0.1:61616");
	}
	
	private class Producer {
		
		@SuppressWarnings("null")
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
				/**
				 * @param transacted      是否开启事物
				 * @param acknowledgeMode 消息确认模式
				 */
				session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
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
						// 提交事务
						session.commit();
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
		Simple_004 simple = new Simple_004();
		simple.createProducer().sendTextMessage("测试测试");
		simple.createConsumer().receiveTextMessage();
	}
}
