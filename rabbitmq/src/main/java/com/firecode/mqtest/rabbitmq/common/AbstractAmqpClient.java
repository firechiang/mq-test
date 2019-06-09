package com.firecode.mqtest.rabbitmq.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class AbstractAmqpClient {
	
	protected String queueName = "test_01";
	protected Connection connect;
	protected Channel channel;
	
	/**
	 * 初始化连接
	 * @throws IOException
	 * @throws TimeoutException
	 */
	@Before
	public void init() throws IOException, TimeoutException{
		// 创建连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("192.168.229.133");
		factory.setPort(5672);
		// 设置虚拟主机
		factory.setVirtualHost("/");
		// 是否自动重连
		factory.setAutomaticRecoveryEnabled(true);
		// 每3秒自动重连一次
		factory.setNetworkRecoveryInterval(3000);
		// 创建连接
		connect = factory.newConnection();
		// 创建通信管道
		channel = connect.createChannel();
		this.before();
		
		// 创建队列时指定一些属性（更详细的属性可参考控制台添加队列的那个位置）
		Map<String, Object>  proper = new HashMap<>();
		// 消息过期时间
		//proper.put("x-message-ttl", "30000");
		// 队列最大长度
		//proper.put("x-max-length", 30000);
		// 队列数据最大长度
	    //proper.put("x-max-length-bytes", 30000);
		// 声明(创建)一个队列
		/**
		 * queue        队列的名称
		 * durable      是否持久化（注意：如果不持久化，服务重启将自动删除）
		 * exclusive    是否单个 Channel 独占监听，如果想要顺序消费消息，就可以设置为 true
		 * autoDelete   队列没有绑定exchange（交换机）是否自动删除消息
		 * arguments    扩展参数
		 */
		DeclareOk queueDeclare = channel.queueDeclare(queueName, true, false, false, proper);
		System.out.println("声明队列："+queueDeclare);
	}
	
	
	/**
	 * 关闭连接
	 * @throws IOException
	 * @throws TimeoutException
	 */
	@After
	public void close() throws IOException, TimeoutException{
		channel.close();
		connect.close();
	}
	
	public void before() throws IOException{}
	
}
