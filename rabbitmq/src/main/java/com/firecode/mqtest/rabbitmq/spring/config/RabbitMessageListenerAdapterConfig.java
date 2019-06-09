package com.firecode.mqtest.rabbitmq.spring.config;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息监听适配器配置和适配器消息转换
 * @author JIANG
 */
//@Configuration
@SuppressWarnings("unused")
public class RabbitMessageListenerAdapterConfig {
	
	
	@Bean
	public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer listener = new SimpleMessageListenerContainer(connectionFactory);
		// 设置要监听的队列（可以写多个）
		listener.setQueueNames("test.spring.queue.topic.bean");
		// 设置消费者数量
		listener.setConcurrentConsumers(1);
		// 设置最大消费者数量
		listener.setMaxConcurrentConsumers(10);
		// 消息确认失败是否重回队列
		listener.setDefaultRequeueRejected(false);
		// 消息确认模式（下面这个是自动确认） 
		listener.setAcknowledgeMode(AcknowledgeMode.AUTO);
	    // 监听器是否外露
		listener.setExposeListenerChannel(true);
		// 如果队列不存在是否自动声明
		//listener.setAutoDeclare(true);
		// 是否自动和Spring容器加载
		//listener.setAutoStartup(true);
		// bean名称
		//listener.setBeanName("");
		// 是否开启事务
		//listener.setChannelTransacted(false);
		/**
		 * 设置消费者唯一标签，可以在控制台：Channels > Consumers 处查看到。（用途：在生产环境使用这个，可以很明确的查看到消费者在消费那个队列）
		 */
		listener.setConsumerTagStrategy(new ConsumerTagStrategy(){
			@Override
			public String createConsumerTag(String queue) {
				
				return queue+"_"+UUID.randomUUID().toString().replaceAll("-", "");
			}
		});
		
		//------------------------------------------适配器相关----------------------------------------------------//
		// 消费者是适配器
		MessageListenerAdapter adapter = messageListenerAdapter();
		// 设置适配器里面消费消息的函数
		adapter.setDefaultListenerMethod("handleMessage");
		/**
		 * 消息被消费前先进行类型转换，以便适配器使用
		 * Spring自带的转换器还有：
		 * Jackson2JsonMessageConverter （对象转JSON消息转换器）
		 * 想要支持JSON转对象加一个入下配置即可，但是要在消息头里面指定类路径：messageProperties.getHeaders().put("__TypeId__", "com.firecode.domain.User");
		 * Jackson2JsonMessageConverter.setJavaTypeMapper(new DefaultJackson2JavaTypeMapper());
		 * 
		 * ------------------------------------------------------------------------------------------------------------------------------------------------
		 *
		 * ContentTypeDelegatingMessageConverter（全局转换器，根据contentType做转发，使用方法如下：）
		 * ContentTypeDelegatingMessageConverter.addDelegate(String contentType, MessageConverter messageConverter)
		 * 
		 */
		adapter.setMessageConverter(new MessageConverter(){

			/**
			 * object转换为message对象
			 */
			@Override
			public Message toMessage(Object object, MessageProperties messageProperties)
					throws MessageConversionException {
				return null;
			}
			/**
			 * message转换为object对象
			 */
			@Override
			public Object fromMessage(Message message) throws MessageConversionException {
				
				return new String(message.getBody(),StandardCharsets.UTF_8);
			}
		});
		// 设置我们的队列名称 和 消费的方法名称，一一对应，就是那个队列就使用那个方法（key=队列名称，value=方法名称）
		//adapter.setQueueOrTagToMethodName(new HashMap<>());
		// 使用适配器的方式消费消息（注意：适配器里面消费消息的函数名称是固定的默认就叫   handleMessage，当然也可以改）
		listener.setMessageListener(adapter);

		return listener;
	}
	
	
	public MessageListenerAdapter messageListenerAdapter(){
		
		MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
		
		return adapter;
	}
	
	
	private static class MessageDelegate {
		
		public void handleMessage(byte[] messageBody){
	    	System.err.println("消费消息："+new String(messageBody,StandardCharsets.UTF_8));
		}
	    
		public void handleMessage(String messageBody){
	    	System.err.println("消费消息："+messageBody);
		}
	}
}
