package com.firecode.mqtest.rocketmq.senior.sequence;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 顺序消息生产者简单使用（消费顺序和生产顺序相同，注意：只能保证每一个队列里面的消息被顺序消费）
 * 注意：注意在发送消息时，要指定消息发送到那个队列
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
		DefaultMQProducer producer = new DefaultMQProducer("test_sequence_producer");
		// 设置NameServer地址，多个以;(分号)分隔
	    producer.setNamesrvAddr("192.168.229.134:9876;192.168.229.132:9876");
	    // 启动生产者
	    producer.start();
	    int size = 10;
	    // 第一组消息
	    Integer queue1 = 1;
	    for (int i = 0; i < size; i++) {
		    /**
		     * @param topic  主题名称（注意：需要手动创建消息主题）
		     * @param tags   标签（消息过滤使用）
		     * @param keys   用户自定义的key（一般用作消息的唯一标识，根据业务来，也可以不唯一）
		     * @param body   消息内容
		     */
		    Message message = new Message("test_sequence_queeu", "tagA", "keyA"+i, ("helloword"+i).getBytes(StandardCharsets.UTF_8));
		    /**
		     * @param message   消息数据
		     * @param selector  消息队列选择器（就是这条消息发送到那个队列上）
		     * @param arg       消息队列选择器所使用的参数（就是这个参数的值，会传到消息队列选择器的回调函数里面去）
		     */
		    SendResult res = producer.send(message,new MessageQueueSelector() {
				
		    	/**
		    	 * 消息队列选择器
		    	 * @param mqs  消息队列集合
		    	 * @param msg  消息数据
		    	 * @param arg  生产者发送消息时传过来的
		    	 * return 消息队列
		    	 */
				@Override
				public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
					Integer index = (Integer)arg;
					return mqs.get(index);
				}
			}, queue1);// queue1是队列的下标（注意：注意这个下标的值不能超过主题里面队列的数量）
		    System.err.println("消息发出："+res);
		}
	    
	    // 第二组消息
	    Integer queue2 = 2;
	    for (int i = 0; i < size; i++) {
		    /**
		     * @param topic  主题名称（注意：需要手动创建消息主题）
		     * @param tags   标签（消息过滤使用）
		     * @param keys   用户自定义的key（一般用作消息的唯一标识，根据业务来，也可以不唯一）
		     * @param body   消息内容
		     */
		    Message message = new Message("test_sequence_queeu", "tagA", "keyB"+i, ("helloword"+i).getBytes(StandardCharsets.UTF_8));
		    /**
		     * @param message   消息数据
		     * @param selector  消息队列选择器（就是这条消息发送到那个队列上）
		     * @param arg       消息队列选择器所使用的参数（就是这个参数的值，会传到消息队列选择器的回调函数里面去）
		     */
		    SendResult res = producer.send(message,new MessageQueueSelector() {
				
		    	/**
		    	 * 消息队列选择器
		    	 * @param mqs  消息队列集合
		    	 * @param msg  消息数据
		    	 * @param arg  生产者发送消息时传过来的
		    	 * return 消息队列
		    	 */
				@Override
				public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
					Integer index = (Integer)arg;
					return mqs.get(index);
				}
			}, queue2);// queue2是队列的下标（注意：注意这个下标的值不能超过主题里面队列的数量）
		    System.err.println("消息发出："+res);
		}
	    // 停止生产者
	    producer.shutdown();
	}
}
