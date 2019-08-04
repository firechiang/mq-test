package com.firecode.mqtest.rocketmq.senior.sql_filter;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * SQL表达式过滤消息消费者简单使用（注意：要在Broker服务端配置开启SQL表达式过滤消息才能使用，否则会报错）
 * 注意：不推荐生产使用，可能存在效率问题
 * 长轮询拉取消息，消息消费失败，重新再消费（推荐生产使用）
 * @author JIANG
 */
public class Consumer {
	
	public static void main(String[] args) throws MQClientException {
		/**
		 * @param consumerGroup 消费者组的名称
		 * 注意：这个名字可以随便起，如果Broker服务端没有配置自动创建，这个是要手动创建的
		 */
		DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer("test_sql__consumer");
		// 设置NameServer地址，多个以;(分号)分隔
		pushConsumer.setNamesrvAddr("192.168.229.134:9876;192.168.229.132:9876");
		// 从最后的位置开始消费（offset偏移量和Kafka一样）
		pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		// 超时时间
		//pushConsumer.setConsumeTimeout(consumeTimeout);
		// 消费者最小并行度
		//pushConsumer.setConsumeThreadMin(1);
		// 消费者最大并行度
		//pushConsumer.setConsumeThreadMax(10);
		// 单个队列并行消费，最大的跨度是多少（可以用于流量控制）
		//pushConsumer.setConsumeConcurrentlyMaxSpan(2);
		// 单个队列一次最都多消费多少个消息（可以用于流量控制）
		//pushConsumer.setPullThresholdSizeForQueue(1);
		// 拉取消息时间的间隔
		//pushConsumer.setPullInterval(1);
		// 一次最低拉取少个消息
		//pushConsumer.setPullBatchSize(1);
		// 一次最多拉取少个消息
		//pushConsumer.setConsumeMessageBatchMaxSize(1);
		// 消息分配策略（）
		//pushConsumer.setAllocateMessageQueueStrategy(AllocateMessageQueueStrategy.);
		// 最大重试消费次数
		//pushConsumer.setMaxReconsumeTimes(3);
		// 消费模式（默认：MessageModel.CLUSTERING 集群模式，同一个消费者组内，负载均衡消费消息，和Kafka一样，这个就是 queue）
		pushConsumer.setMessageModel(MessageModel.CLUSTERING);
		// 消费模式（MessageModel.BROADCASTING 广播模式，同一个消费者组内，每个消费者都会接收到消息，这个就是 topic）
		//pushConsumer.setMessageModel(MessageModel.BROADCASTING);
		/**
		 * 订阅消息主题和SQL过滤消息规则（注意：生产者是发了十条消息的，根据下面的规则，消费者应该只能消费4条，就是index的值从0到3）
		 * @param topic             主题名称
		 * @param messageSelector   SQL表达式过滤规则
		 * 
		 * 数字可以使用：     >，>=，<，<=，=，between
		 * 判断空可使用：     is null，is not null
		 * 逻辑符号可使用：and，or，not
		 */
		pushConsumer.subscribe("test_sql_queue", MessageSelector.bySql("index between 0 and 3"));
		// 注册消息监听器（注意：MessageListenerConcurrently是并行消费接口）
		pushConsumer.registerMessageListener(new MessageListenerConcurrently() {
			/**
			 * @param msgs     消息集合（为什么是消息集合，因为生产者发送消息时，可能是一次性发了多条消息。）
			 * @param context  消费者全局上下文
			 */
			@Override
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				MessageExt me = msgs.get(0);
				try{
					String topic = me.getTopic();
					String body = new String(me.getBody(),StandardCharsets.UTF_8);
					String tags = me.getTags();
					String keys = me.getKeys();
					System.out.println("消费消息：topic："+topic+"，tags："+tags+"，keys："+keys+"，body："+body);
					
				}catch(Exception e){
					e.printStackTrace();
					// 消息是第几次重试消费
					int reconsumeTimes = me.getReconsumeTimes();
					System.err.println("消息消费重试次数："+reconsumeTimes);
					// 消息重试消费3次以后，不再重试消费（注意：重试次数可以使用pushConsumer.setMaxReconsumeTimes(3);指定）
					if(reconsumeTimes == 3) {
						return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					}
					// 消息消费失败，过一段时间进行重试（重试策略在Broker服务端配置文件里面可以配置）
					return ConsumeConcurrentlyStatus.RECONSUME_LATER;
				}
				// 消息处理成功
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		// 取消订阅主题
		//pushConsumer.unsubscribe(topic);
		// 开始消费
		pushConsumer.start();
	}

}
