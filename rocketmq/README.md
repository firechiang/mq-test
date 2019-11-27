#### 一、[Windows开发搭建][1]
#### 二、[单节点搭建][2]
#### 三、[多主多从集群搭建][3]
#### RocketMQ概念模型（注意：长轮询拉取消息，如果没有消息，Broker服务端会让消费端连接进入等待状态，默认好像是等待5秒，再返回，具体代码在：https://github.com/apache/rocketmq/blob/master/broker/src/main/java/org/apache/rocketmq/broker/longpolling/PullRequestHoldService.java 67行）
- 1，Broker：MQ消息服务（中转角色，用于消息存储与生产消息转发） 
- 2，Producer 负责生产消息，一般由业务系统负责生产消息
- 3，Producer Group 生产者集合，一般用于发送一类消息
- 4，Consumer 负责消费消息，一般是后台系统负责异步消费（注意：这个是异步消费消息）
- 5，Push Consumer 消费者中的一种，需要注册消费监听器（注意：这种消费模型不是服务端向消费端推送消息，而是消费端长轮询拉取消息，且Broker服务端自动处理Offset（偏移量），所以推荐生产使用）
- 6，Pull Consumer 消费者中的一种，消费者主动拉取消息进行消费（注意：这种消费模型要手动管理Offset（偏移量），所以不推荐生产使用）
- 7，Consumer Group 消费者集合，一般用于接收一类消息进行消费（和Kafka相同）

#### Offset偏移量说明（注意：RocketMQ的消息偏移量和Kafka的基本一样）
- 1，MessageModel.CLUSTERING（Queue模式消费消息）使用 RemoteBrokerOffsetStore 远程文件存储Offset（偏移量储存在Broker服务端，以便解决重复消费问题）
- 2，MessageModel.BROADCASTING（Topic模式消费消息）使用 LocalFileOffsetStore 本地文件存储Offset（偏移量储存在消费者本地）

#### NameServer 说明
- 1，NameServer的部署都是相互独立的，它没有集群的概念。
- 2，RocketMQ的Broker服务会将元数据发送到每一个NameServer。读取NameServer的数据的时候任选一台读取

[1]: https://github.com/firechiang/mq-test/tree/master/rocketmq/docs/windows-single-node.md
[2]: https://github.com/firechiang/mq-test/tree/master/rocketmq/docs/bin-single-node.md
[3]: https://github.com/firechiang/mq-test/tree/master/rocketmq/docs/bin-cluster-node.md