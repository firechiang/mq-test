package com.firecode.mqtest.rabbitmq.feature.confirm_message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.rabbitmq.client.ConfirmListener;

/**
 * 监听消息是否投递成功
 * 生产者
 * @author JIANG
 */
public class Producer extends AbstractClient {
	
	
	@Test
	public void test() throws IOException, InterruptedException {
		// 开启发送方监听消息是否投递成功模式
		channel.confirmSelect();
		// 添加监听器，监听消息是否投递成功
		channel.addConfirmListener(new ConfirmListener(){
			/**
			 * @param deliveryTag  消息唯一标签。
			 * @param 消息确认有可能是批量确认的，是否批量确认在于返回的multiple的参数，
			 *        此参数为bool值，如果true表示批量执行了deliveryTag这个值以前的所有消息，如果为false的话表示单条确认。
			 * 
			 * 消息投递成功回调。
			 */
			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				System.out.println("deliveryTag："+deliveryTag+"，消息投递成功，是否批量成功："+multiple);
			}
			/**
			 * @param deliveryTag  消息唯一标签。
			 * @param 消息确认有可能是批量确认的，是否批量确认在于返回的multiple的参数，
			 *        此参数为bool值，如果true表示批量执行了deliveryTag这个值以前的所有消息，如果为false的话表示单条确认。
			 *        
			 * 消息投递失败回调。
			 */
			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				System.err.println("deliveryTag："+deliveryTag+"，消息投递失败，是否批量失败："+multiple);
			}
		});
		String msg = "消息";
		int size = 10;
		for(int i=0;i<size;i++) {
			/**
			 * exchange     交换机名称（注意：如果没有指定交换机名称，默认使用 (AMQP default)交换机）
			 * 
			 * routingKey   路由的名称（注意：如果没有指定交换机名称，默认使用 (AMQP default)交换机，
			 *              它会直接将"路由的名称"当队列的名称来用，也就是直接将消息发送到这个"路由的名称"上，前提是这个队列名称已存在）
			 *              
			 * props        消息的一些附加属性
			 * 
			 * body         消息数据
			 */
			channel.basicPublish(exchangeName,routingKey, null, (msg+i).getBytes(StandardCharsets.UTF_8));
		}
		TimeUnit.DAYS.sleep(1L);
	}
}
