package com.firecode.mqtest.rocketmq.helloword.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;

/**
 * @see https://github.com/apache/rocketmq/blob/master/example/src/main/java/org/apache/rocketmq/example/simple/PullConsumer.java
 * 
 * 主动拉取消息简单使用，这种方式要手动管理Offset（偏移量），想消费那个偏移量就消费那个偏移量。
 * 注意：这个不推荐生产使用
 * 
 * @author JIANG
 */
public class PullConsumer {

	private static final Map<MessageQueue, Long> OFFSE_TABLE = new HashMap<MessageQueue, Long>();

	public static void main(String[] args) throws MQClientException {
		/**
		 * @param consumerGroup 消费者组的名称（这个名字可以随便起）
		 */
		DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer("helloword_pull_consumer");
		// 设置NameServer地址，多个以;(分号)分隔
		pullConsumer.setNamesrvAddr("192.168.229.133:9876");
		// 启动消费者
		pullConsumer.start();
		/**
		 * @param topic 主题名称
		 * 注意：这种方式，如果没有这个主题会报错，所以要先创建主题
		 */
		// 获取主题里面的所有队列（默认会有4个，我们在Broker服务端配置文件里配置了）
		Set<MessageQueue> mqs = pullConsumer.fetchSubscribeMessageQueues("helloword_topic");
		// 遍历队列进行数据拉取
		for (MessageQueue mq : mqs) {
			System.out.printf("Consume from the queue: %s%n", mq);
			SINGLE_MQ: while (true) {
				try {
					/**
					 * @param MessageQueue mq       要获取那个队列里面的数据
					 * @param String subExpression  只要带有那些tag（标签）的数据
					 * @param long offset           从那个偏移量开始获取数据
					 * @param int maxNums           一次最多拉取多少条数据
					 */
					PullResult pullResult = pullConsumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);

					System.out.printf("%s%n", pullResult);
					putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
					switch (pullResult.getPullStatus()) {
					case FOUND:
						break;
					case NO_MATCHED_MSG:
						break;
					case NO_NEW_MSG:
						break SINGLE_MQ;
					case OFFSET_ILLEGAL:
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static long getMessageQueueOffset(MessageQueue mq) {
		Long offset = OFFSE_TABLE.get(mq);
		if (offset != null)
			return offset;

		return 0;
	}

	private static void putMessageQueueOffset(MessageQueue mq, long offset) {
		OFFSE_TABLE.put(mq, offset);
	}

}
