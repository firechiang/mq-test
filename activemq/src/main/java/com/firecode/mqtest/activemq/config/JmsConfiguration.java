package com.firecode.mqtest.activemq.config;

import javax.jms.Queue;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
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

@EnableConfigurationProperties({ActiveMQProperties.class})
@Configuration
@EnableJms
public class JmsConfiguration {
	
    @Bean
    public Queue queue(){
        return new ActiveMQQueue("simple.queue");
    }
    @Bean
    public Topic topic(){
        return new ActiveMQTopic("simple.topic");
    }
    
    public ActiveMQConnectionFactory connectionFactory(ActiveMQProperties properties) {
    	
        return new ActiveMQConnectionFactory(properties.getUser(), properties.getPassword(), properties.getBrokerUrl());
    }
    
    @Bean("jmsListenerContainerTopic")
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ActiveMQProperties properties,PooledConnectionFactory pooledConnectionFactory) {
    	ActiveMQConnectionFactory factory = connectionFactory(properties);
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
        //CachingConnectionFactory bean = new CachingConnectionFactory();
        bean.setPubSubDomain(true);//开启 topic 主题消息
        //设置连接数
        bean.setConcurrency("3-10");
        //重连间隔时间
        bean.setRecoveryInterval(1000L);
        //消息确认模式
        bean.setSessionAcknowledgeMode(4);//单条消息确认 activemq 独有
        bean.setConnectionFactory(cachingFactory);
        return bean;
    }
    
    @Bean("jmsListenerContainerQueue")
    public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ActiveMQProperties properties) {
    	ActiveMQConnectionFactory factory = connectionFactory(properties);
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(factory);
        return bean;
    }
	
	
	
	
}
