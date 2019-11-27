package com.firecode.mqtest.rocketmq.spring.transaction;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.firecode.mqtest.rocketmq.domain.MsgContent;

/**
 * 发送事物消息
 * 注意：事物消息只能保证消息发送和数据库的某个操作一起成功或失败
 * @author JIANG
 */
@RestController
@RocketMQTransactionListener(txProducerGroup = TransactionProducerTestQueue.QUEUE_NAME_GROUP,corePoolSize=4,maximumPoolSize=100)
public class TransactionProducerTestQueue implements RocketMQLocalTransactionListener {
	
	@Autowired
    private RocketMQTemplate rocketMQTemplate;
	
	private String[] names = {"maomoa","tianti","jiang","kajdiaj","zssdfsfs"};
	
	private String[] tags =  {"TagA", "TagB", "TagC", "TagD", "TagE"};
	
	public static final String QUEUE_NAME = "test_transaction_queue";
	
	public static final String QUEUE_NAME_GROUP = "test_transaction_queue_group";
	
	@GetMapping("/senTMsg")
	public SendResult sendMessage(@RequestParam(name="desc",required = false) String desc) throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		MsgContent msgContent = new MsgContent(names[random.nextInt(5)], System.currentTimeMillis(),desc);
		Message<MsgContent> msg = MessageBuilder.withPayload(msgContent).build();
		// 发送事物消息
		TransactionSendResult syncSend = rocketMQTemplate.sendMessageInTransaction(QUEUE_NAME_GROUP, QUEUE_NAME+":"+tags[random.nextInt(5)],msg,null);
		System.out.printf("------ send Transactional msg body = %s , sendResult=%s %n",msg.getPayload(), syncSend.getSendStatus());
		return syncSend;
	}
	
	/**
	 * 消息发送成功后的回调函数，我们在这里可以做数据库的操作再提交消息事物来确认消息是否有效
	 * 注意：这个函数的调用和消息发送所在函数的调用是用同步的，并非异步（也就是消息发送完成以后，立马提交提交消息的事物）
	 */
	@Override
	public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
		System.err.println("消息已发出，之后逻辑已处理完成，msg："+msg+"，arg："+arg);
		GenericMessage<byte[]> message = (GenericMessage<byte[]>)msg;
		MsgContent msgContent = JSONObject.parseObject(message.getPayload(), MsgContent.class);
		// 这里可以作数据库的落库等操作
		if(null != msgContent) {
			// 提交消息事物，让消费端可以看到消息并消费
			return RocketMQLocalTransactionState.COMMIT;
		}
		if(msg.equals("b")) {
			// 回滚消息事物，让消息发送失败，消费端将看不到消息
			return RocketMQLocalTransactionState.ROLLBACK;
		}
		// 丢弃消息，消费端将看不到消息
		return RocketMQLocalTransactionState.UNKNOWN;
		
	}

	/**
	 * 消息发送期间遇到异常，检查消息是否已提交事物
	 */
	@Override
	public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
		System.err.println("检查消息是否已提交事物："+msg);
		// 这里查询消息是否发送（注意：上一个回调函数，应记录事物是否提交（比如：如果事物提交了，在数据库里面应该有一条记录））
		// 查询到事物已经提交了（提交事物）
		if("a".contentEquals("b")) {
			return RocketMQLocalTransactionState.COMMIT;
		}
		// 查询到事物没有提交，回滚消息
		if("b".contentEquals("c")) {
			return RocketMQLocalTransactionState.ROLLBACK;
		}
		// 查询到消息已失效，丢弃消息
		//return RocketMQLocalTransactionState.UNKNOWN;
		return RocketMQLocalTransactionState.ROLLBACK;
	}
	
	
}
