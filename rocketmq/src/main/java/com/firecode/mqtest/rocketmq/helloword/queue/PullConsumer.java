package com.firecode.mqtest.rocketmq.helloword.queue;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;

/**
 * 主动拉取消息简单使用，这种方式要手动管理Offset（偏移量），想消费那个偏移量就消费那个偏移量
 * 注意：这个不推荐生产使用
 * @author JIANG
 */
public class PullConsumer {
	
	public static void main(String[] args) {
		/**
		 * @param consumerGroup 消费者组的名称（这个名字可以随便起）
		 */
		DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer("helloword_pull_consumer");
		// 设置NameServer地址，多个以;(分号)分隔
		pullConsumer.setNamesrvAddr("192.168.229.133:9876");
	}

}
