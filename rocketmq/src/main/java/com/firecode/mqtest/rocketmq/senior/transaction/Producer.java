package com.firecode.mqtest.rocketmq.senior.transaction;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 异步事务消息生产者（消息发送后，异步提交事务和回滚事务）
 * @author JIANG
 */
public class Producer {
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		/**
		 * @param producerGroup  生产者组名称（这个名字可以随便起）
		 */
		TransactionMQProducer producer = new TransactionMQProducer("test_tx_group");
		/**
		 * 创建一个线程池
		 */
		ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000),Executors.defaultThreadFactory());
		// 设置NameServer地址，多个以;(分号)分隔
	    producer.setNamesrvAddr("192.168.229.134:9876;192.168.229.132:9876");
	    // 设置线程池
	    producer.setExecutorService(executorService);
	    // 消息发送后，异步回调这个监听器（注意：消息的发送可以和这个监听器要处理的内容在同一个事务内完成）
	    producer.setTransactionListener(new TransactionListener() {
	        private AtomicInteger transactionIndex = new AtomicInteger(0);
	        
	        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();
	        
			/**
			 * 消息发送后，本地提交事务之前回调
			 * @param msg  消息的内容
			 * @param arg  发送消息时给的参数
			 * return 
			 * 
			 */
			@Override
			public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
				// 这里可以作数据库的落库等操作
				System.err.println("消息已发出，之后逻辑已处理完成，msg："+msg+"，arg："+arg);
				
		        int value = transactionIndex.getAndIncrement();
		        int status = value % 3;
		        localTrans.put(msg.getTransactionId(), status);
		        return LocalTransactionState.UNKNOW;
				//LocalTransactionState.COMMIT_MESSAGE（事务提交表示消息发送成功，消费者可消费）
				//LocalTransactionState.ROLLBACK_MESSAGE（回滚消息，删除消息数据，消费者看不到消息，也不可以消费）
				//LocalTransactionState.UNKNOW（消息不可达，那么消费者就看不到消息，也不可以消费）
			}
			/**
			 * Broker服务如果长时间没有收到事务提交成功的请求，就会发起验证事务是否提交成功的回调
			 * 注意：要测试这个回调的话，只要上面executeLocalTransaction函数的返回值设置为LocalTransactionState.UNKNOW，就会回调这个
			 * @param msg  消息的内容
			 * @param arg  发送消息时给的参数
			 * return 
			 * 
			 */
			@Override
			public LocalTransactionState checkLocalTransaction(MessageExt msg) {
				System.err.println("回调消息检查，msg："+msg);
				Integer status = localTrans.get(msg.getTransactionId());
	            if (null != status) {
	                switch (status) {
	                    case 0:
	                        return LocalTransactionState.UNKNOW;
	                    case 1:
	                        return LocalTransactionState.COMMIT_MESSAGE;
	                    case 2:
	                        return LocalTransactionState.ROLLBACK_MESSAGE;
	                }
	            }
	            return LocalTransactionState.COMMIT_MESSAGE;
			    //LocalTransactionState.COMMIT_MESSAGE（事务提交表示消息发送成功，消费者可消费）
				//LocalTransactionState.ROLLBACK_MESSAGE（回滚消息，删除消息数据，消费者看不到消息，也不可以消费）
				//LocalTransactionState.UNKNOW（消息不可达，就是消息不需要发送，那么消费者就看不到消息，也不可以消费）
			}
		});
	    // 启动生产者
	    producer.start();
	    int size = 1;
	    for (int i = 0; i < size; i++) {
		    /**
		     * @param topic  主题名称（注意：需要手动创建消息主题）
		     * @param tags   标签（消息过滤使用）
		     * @param keys   用户自定义的key（一般用作消息的唯一标识，根据业务来，也可以不唯一）
		     * @param body   消息内容
		     */
		    Message message = new Message("test_queue", "tagA", "key"+i, ("helloword"+i).getBytes(StandardCharsets.UTF_8));
		    /**
		     * 发送事务消息
		     * @param msg  消息
		     * @param arg  事务监听器回调参数（就是在事务监听器的回调函数里面可以获取得到这个值）
		     */
		    SendResult res = producer.sendMessageInTransaction(message, i);
		}
	    // 停止生产者
	    //producer.shutdown();
	}
}
