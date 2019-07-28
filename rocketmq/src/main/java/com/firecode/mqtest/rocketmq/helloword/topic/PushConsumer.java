package com.firecode.mqtest.rocketmq.helloword.topic;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * 长轮询拉取消息，消息消费失败，重新再消费（推荐生产使用）
 * @author JIANG
 */
public class PushConsumer {
	
	public static void main(String[] args) throws MQClientException {
		/**
		 * @param consumerGroup 消费者组的名称（这个名字可以随便起）
		 */
		DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer("helloword_pull_consumer");
		// 设置NameServer地址，多个以;(分号)分隔
		pushConsumer.setNamesrvAddr("192.168.229.133:9876;192.168.229.133:9876");
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
		//pushConsumer.setMessageModel(MessageModel.CLUSTERING);
		// 消费模式（MessageModel.BROADCASTING 广播模式，同一个消费者组内，每个消费者都会接收到消息，这个就是 topic）
		pushConsumer.setMessageModel(MessageModel.BROADCASTING);
		/**
		 * 订阅消息主题和过滤消息规则
		 * @param topic         主题名称
		 * @param subExpression 只消费带有那些tag（标签）的消息（注意：这里可以写正则表达式，如果写*表示匹配所有消息。
		 *                       要同时接收多个tag消息，可以这样写：tagA || tagB。
		 *                       tag（标签）在生产消息时可以指定） 。
		 */
		pushConsumer.subscribe("helloword_topic", "tagA");
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
					// 模拟消息消费失败
					if("key0".equals(keys)){
						throw new RuntimeException();
					}
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
