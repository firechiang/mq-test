package com.firecode.mqtest.activemq.helloworld;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 监听器消费
 * 
 * @author JIANG
 *
 */
public class Simple_002 {

	// 配置 conf/activemq.xml
	private ConnectionFactory factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://127.0.0.1:61616");

	public Simple_002() throws JMSException {
	Connection connection = null;
	Session session = null;
	MessageConsumer consumer = null;
		connection = factory.createConnection();
		// 消费者必须启动消息，发送者会检查连接是否启动
		connection.start();
		session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		Destination destination = session.createQueue("test_001");
		// 消息消费者
		consumer = session.createConsumer(destination);
		// 监听器<p>可注册多个</p>
		consumer.setMessageListener((message) -> {
			TextMessage msg = (TextMessage) message;
			try {
				System.err.println(msg.getText());
				// 确认消息已被消费，activemq 删除对应消息
				message.acknowledge();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public static void main(String[] args) throws JMSException, IOException  {
		Simple_002 simple = new Simple_002();
		System.in.read();
	}
}
