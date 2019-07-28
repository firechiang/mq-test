package com.firecode.mqtest.rocketmq.helloword.topic;

import org.apache.rocketmq.client.consumer.MQPullConsumer;
import org.apache.rocketmq.client.consumer.MQPullConsumerScheduleService;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.PullTaskCallback;
import org.apache.rocketmq.client.consumer.PullTaskContext;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * @see https://github.com/apache/rocketmq/blob/master/example/src/main/java/org/apache/rocketmq/example/simple/PullScheduleService.java
 * 
 * 以定时任务的方式主动拉取消息简单使用，这种方式要手动更新Offset（偏移量）。
 * 注意：这个不推荐生产使用，但是也要看情况
 * @author JIANG
 */
public class PullScheduleService {

    public static void main(String[] args) throws MQClientException {
		/**
		 * @param consumerGroup 消费者组的名称（这个名字可以随便起）
		 */
        final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("helloword_pull_consumer");
        // topic模式
        scheduleService.setMessageModel(MessageModel.BROADCASTING);
		/**
		 * 注册拉取消息的定时任务
		 * @param topic 主题名称
		 * 注意：这种方式，如果没有这个主题会报错，所以要先创建主题
		 */
		// 获取主题里面的所有队列（默认会有4个，我们在Broker服务端配置文件里配置了）
        scheduleService.registerPullTaskCallback("TopicTest", new PullTaskCallback() {

            @Override
            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();
                try {

                    long offset = consumer.fetchConsumeOffset(mq, false);
                    if (offset < 0){
                    	offset = 0;
                    }
					/**
					 * @param MessageQueue mq       要获取那个队列里面的数据
					 * @param String subExpression  只要带有那些tag（标签）的数据
					 * @param long offset           从那个偏移量开始获取数据
					 * @param int maxNums           一次最多拉取多少条数据
					 */
                    PullResult pullResult = consumer.pull(mq, "*", offset, 32);
                    System.out.printf("%s%n", offset + "\t" + mq + "\t" + pullResult);
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                    // 更新偏移量
                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
                    // 设置再过多长时间后，再次拉取消息
                    context.setPullNextDelayTimeMillis(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        scheduleService.start();
    }
}