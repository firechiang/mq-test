package com.firecode.mqtest.rocketmq.senior.sequence;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * 顺序消息消费者简单使用（消费顺序和生产顺序相同，注意：只能保证每一个队列里面的消息被顺序消费）。
 * 长轮询拉取消息，消息消费失败，重新再消费（推荐生产使用）
 * @author JIANG
 */
public class Consumer {
	
	public static void main(String[] args) throws MQClientException {
		/**
		 * @param consumerGroup 消费者组的名称
		 * 注意：这个名字可以随便起，如果Broker服务端没有配置自动创建，这个是要手动创建的
		 */
		DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer("test_sequence_producer_consumer");
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
		 * 订阅消息主题和过滤消息规则
		 * @param topic         主题名称（注意：需要手动创建消息主题）
		 * @param subExpression 只消费带有那些tag（标签）的消息（注意：这里可以写正则表达式，如果写*表示匹配所有消息。
		 *                      要同时接收多个tag消息，可以这样写：tagA || tagB。
		 *                       tag（标签）在生产消息时可以指定） 。
		 */
		pushConsumer.subscribe("test_sequence_queeu", "tagA");
		// 注册消息监听器（注意：MessageListenerOrderly是单线程顺序消费接口）
		pushConsumer.registerMessageListener(new MessageListenerOrderly() {
			/**
			 * @param msgs     消息集合（为什么是消息集合，因为生产者发送消息时，可能是一次性发了多条消息。）
			 * @param context  消费者全局上下文
			 */
			@Override
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
				//context.setAutoCommit(true); 自动提交
				for(MessageExt me:msgs){
					System.out.println("消费消息，MessageExt："+me);
				}
				// 消息处理成功
				return ConsumeOrderlyStatus.SUCCESS;
			}
		});
		// 取消订阅主题
		//pushConsumer.unsubscribe(topic);
		// 开始消费
		pushConsumer.start();
	}

}
