package com.firecode.mqtest.rocketmq.senior.sql_filter;

import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * SQL表达式过滤消息生产者简单使用（注意：要在Broker服务端配置开启SQL表达式过滤消息才能使用，否则会报错）
 * 注意：不推荐生产使用，可能存在效率问题
 * @author JIANG
 */
public class Producer {
	
	/**
	 * @param args
	 * @throws MQClientException     RocketMQ连接失败抛出
	 * @throws RemotingException     消息发送过程网络中断抛出
	 * @throws MQBrokerException     消息到达Broker，但Broker存储失败抛出
	 * @throws InterruptedException  多线程打断时抛出 
	 */
	public static void main(String[] args) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		/**
		 * @param producerGroup 生产者组的名称（这个名字可以随便起）
		 */
		DefaultMQProducer producer = new DefaultMQProducer("test_sql_producer");
		// 设置NameServer地址，多个以;(分号)分隔
	    producer.setNamesrvAddr("192.168.229.134:9876;192.168.229.132:9876");
	    // 启动生产者
	    producer.start();
	    int size = 10;
	    for (int i = 0; i < size; i++) {
		    /**
		     * @param topic  主题名称（注意：需要手动创建消息主题）
		     * @param tags   标签（消息过滤使用）
		     * @param keys   用户自定义的key（一般用作消息的唯一标识，根据业务来，也可以不唯一）
		     * @param body   消息内容
		     */
		    Message message = new Message("test_sql_queue", "tagA", "key"+i, ("helloword"+i).getBytes(StandardCharsets.UTF_8));
		    // 为消息添加一些自定义属性，以供消费端使用SQL表达式过滤
		    message.putUserProperty("index", String.valueOf(i));
		    // 发送消息
		    SendResult res = producer.send(message);
	        // oneway发送消息，只要不抛异常就是成功（注意：这种方式发送消息效率最高，就是可靠信稍微差一点），看业务需求使用
	        //producer.sendOneway(message);
		    System.err.println("消息发出："+res);
		}
	    // 停止生产者
	    producer.shutdown();
	}
}
