package com.firecode.mqtest.activemq.config;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@EnableConfigurationProperties({ ActiveMQProperties.class })
@Configuration
@EnableJms
public class JmsConfiguration {

	@Bean
	public Queue queue() {
		return new ActiveMQQueue("simple.queue");
	}

	@Bean
	public Topic topic() {
		return new ActiveMQTopic("simple.topic");
	}

	public ActiveMQConnectionFactory connectionFactory(ActiveMQProperties properties) {
		return new ActiveMQConnectionFactory(properties.getUser(), properties.getPassword(), properties.getBrokerUrl());
	}

	@Bean("jmsListenerContainerTopic")
	public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ActiveMQProperties properties,
			PooledConnectionFactory pooledConnectionFactory) {
		ActiveMQConnectionFactory factory = connectionFactory(properties);
		// 配置消息重试机制
		factory.setRedeliveryPolicy(redeliveryPolicy());
		pooledConnectionFactory.setConnectionFactory(factory);
		CachingConnectionFactory cachingFactory = new CachingConnectionFactory();
		cachingFactory.setTargetConnectionFactory(pooledConnectionFactory);
		cachingFactory.setSessionCacheSize(3);
		// 确定服务器端是否产生了过量的消息堆积，需要减慢消息生产端的生产速度
		factory.setProducerWindowSize(102400);
		factory.setAlwaysSyncSend(true);
		// 异步方式
		factory.setUseAsyncSend(true);
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		// CachingConnectionFactory bean = new CachingConnectionFactory();
		// false监听queue，true监听 topic
		bean.setPubSubDomain(true);
		// 设置连接数
		bean.setConcurrency("3-10");
		// 重连间隔时间
		bean.setRecoveryInterval(1000L);
		// 消息确认模式
		bean.setSessionAcknowledgeMode(4);// 单条消息确认 activemq 独有
		bean.setConnectionFactory(cachingFactory);
		return bean;
	}

	@Bean("jmsListenerContainerQueue")
	public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ActiveMQProperties properties) {
		ActiveMQConnectionFactory factory = connectionFactory(properties);
		// 配置消息重试机制
		factory.setRedeliveryPolicy(redeliveryPolicy());
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		bean.setConnectionFactory(factory);
		// 开启持久订阅
		bean.setSubscriptionDurable(true);
		return bean;
	}

	@Bean("jmsListenerContainerQueueTest")
	public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		// 启用事物（消费者收到消息后给服务端发送一个确认，服务端收到确认后才将消息从服务端删除）
		// jmsTemplate.setSessionTransacted(true); // 手动确认消息（这个和上面的事物不知道会不会有冲突）
		// jmsTemplate.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		return jmsTemplate;
	}
	
    /**
     * 消息重试配置（注意：可能只有事物消息才有重试机制，要测试才知道）
     * @return
     */
	//@Bean
    public RedeliveryPolicy redeliveryPolicy(){
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		// 是否在每次尝试重新发送失败后，增长这个等待时间
		redeliveryPolicy.setUseExponentialBackOff(true);
		// 重发最大拖延时间（-1表示没有拖延，但是一定要setUseExponentialBackOff是true才会生效）
		redeliveryPolicy.setMaximumRedeliveryDelay(-1);
		// 最大重发次数（默认6，-1不限制重发次数）
		redeliveryPolicy.setMaximumRedeliveries(10);
		// 重发时间间隔（默认1）
		redeliveryPolicy.setInitialRedeliveryDelay(5);
		// 第一次失败后，重新发送之前等待500毫秒，第二次失败再等待500*2（注意：2就是这里的值）
		redeliveryPolicy.setBackOffMultiplier(2);
        //是否避免消息碰撞
        redeliveryPolicy.setUseCollisionAvoidance(false);
        return redeliveryPolicy;
    }
}
