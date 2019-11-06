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
/**
 * 主动消费
 * 注意：ActiviMQ可以使用通配符订阅
 * @author JIANG
 *
 */
public class Simple_001 {
	
	//配置 conf/activemq.xml
	private ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://127.0.0.1:61616");
	
	private class Producer {
		public void sendTextMessage(String content){
			Connection connection = null;
			Session session = null;
			MessageProducer producer = null;
			try{
				connection = factory.createConnection();
				//消费者必须启动消息，发送者会检查连接是否启动
				connection.start();
				/**
				 * transacted 是否支持事物<p>消息有批量发送建议使用事物，因为activemq默认有缓冲区</p>
				 *     true  -支持事物 第二个参数默认无效 ，开启事物后send() 函数后需要 session.commit()
				 *     false -不支持事物，常用，第二个参数必须传递且有效
				 * 以上针对消息生产者，消费者默认是没有事物的  
				 * 
				 *   
				 * acknowledgeMode  
				 *     Session.AUTO_ACKNOWLEDGE    消息的消费者处理消息后，自动确认（MQ自动调用message.acknowledge()），常用，商业开发 （不推荐）
				 *     Session.CLIENT_ACKNOWLEDGE  客户端手动确认消息（手动调用message.acknowledge()了，才不会重发）
				 *     Session.DUPS_OK_ACKNOWLEDGE 有副本的客户端手动确认（批量确认，有延迟确认的特点ActiviMQ根据内部算法，在收到一定数量的消息进行批量确认），一个消息可以多次处理，可以降低 session 消耗，可以容忍重复消息时使用   （不推荐）
				 *     Session.SESSION_TRANSACTED  事物消息提交确认（当Session使用事物时，请使用此模式）
				 */
				session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
				//消息目的地<p>创建消息目的地参数可以为 null，可在消息发送时指定 消息目的地</p>
				Destination destination = session.createQueue("test_001");
				//消息发送者
				producer = session.createProducer(destination);
				//消息体
				Message message = session.createTextMessage(content);
				//发送消息
				producer.send(message);
				/**
				 * DeliveryMode.PERSISTENT 持久化
				 * 优先级
				 * 有效期
				 */
				//producer.send(destination, message, DeliveryMode.PERSISTENT, 1, 30);
				
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
				//消费者必须启动消息，发送者会检查连接是否启动
				connection.start();
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Destination destination = session.createQueue("test_001");
				//消息消费者
				consumer = session.createConsumer(destination);
				//获取队列中的消息，执行一次拉取一次消息，如果无消息则阻塞当前线程 （一般测试用）
				Message message = consumer.receive();
				//处理消息
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
		Simple_001 simple = new Simple_001();
		simple.createProducer().sendTextMessage("测试测试");
		simple.createConsumer().receiveTextMessage();
	}
}
