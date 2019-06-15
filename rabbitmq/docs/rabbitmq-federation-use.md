#### 一、RabbitMQ集群双活同步数据架构图
![object](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/rabbitmq-federation.svg)
#### 二、安装 federation 插件，所有集群的所有节点都要安装（注意：安装插件时 RabbitMQ 必须是启动的否则无法安装）
```bash
# 查看 RabbitMQ 自带的插件列表
$ rabbitmq-plugins list
# 安装 federation 插件
$ rabbitmq-plugins enable rabbitmq_federation
# 安装 federation 管理插件
$ rabbitmq-plugins enable rabbitmq_federation_management
```

#### 三、配置消息同步，原理就是以消费者的方式拉取数据（注意：以下操作在想要同步数据的节点上操作）
##### 3.1，创建一个交换机
![image](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/federation01.png)
##### 3.2，创建一个队列
![image](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/federation02.png)
##### 3.3，在交换机上绑定队列（Exchanges > test.exchange.federation）
![image](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/federation03.png)
##### 3.4，创建 Federation Upstreams（数据来源）（Admin > 右边的 Federation Upstreams）
![image](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/federation04.png)
##### 3.5，创建同步数据配置规则（Admin > 右边的 Policies）
![image](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/federation05.png)
##### 3.6，看看Federation Status（同步状态）是否有数据了（Admin > 右边的 Federation Status）
![image](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/federation06.png)
##### 3.7，到被同步数据的节点上看看是否自动创建了交换机和队列以及有2个连接和1个消费者（都有的话说明数据同步配置好了）
![image](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/federation07.png)