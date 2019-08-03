#### 一、集群节点分布，主节点可读可写，从节点只读，不能自动切换（注意：NameServer的部署都是相互独立的，它没有集群的概念。RocketMQ的Broker服务会将元数据发送到每一个NameServer。读取NameServer的数据的时候任选一台读取。）
```bash
----------|----------|---------|--------------|
          |  Master  |  Slave  |  NameServer  |
----------|----------|---------|--------------|
server001 |    Y     |         |              |
----------|----------|---------|--------------|
server002 |    Y     |         |              |
----------|----------|---------|--------------|
server003 |          |    Y    |       Y      |
----------|----------|---------|--------------|
server004 |          |    Y    |       Y      |
----------|----------|---------|--------------|
```

#### 二、下载安装 RocketMQ，[官方安装文档](http://rocketmq.apache.org/docs/quick-start)
```bash
$ cd /home/tools
$ wget -P /home/tools http://mirrors.tuna.tsinghua.edu.cn/apache/rocketmq/4.4.0/rocketmq-all-4.4.0-bin-release.zip
$ yum install -y unzip                                         # 安装解压ZIP文件工具（如果已经有了，就不用再安装了）
$ unzip rocketmq-all-4.4.0-bin-release.zip -d ../              # 解压到上层目录
```

#### 三、创建RocketMQ数据存储目录（注意：这些目录都会在下面的配置文件中使用）
```bash
$ mkdir /home/rocketmq-all-4.4.0-bin-release/data              # 数据存储路径
$ mkdir /home/rocketmq-all-4.4.0-bin-release/data/commitlog    # commitLog 存储路径
$ mkdir /home/rocketmq-all-4.4.0-bin-release/data/consumequeue # 消费队列存储路径存储路径
$ mkdir /home/rocketmq-all-4.4.0-bin-release/data/index        # 消息索引存储路径
$ mkdir /home/rocketmq-all-4.4.0-bin-release/logs              # RocketMQ日子存储目录
```

#### 四、修改[vi /home/rocketmq-all-4.4.0-bin-release/conf/my-cluater/broker-m1.conf]配置（注意：这是第一个主节点的配置，它对应的机器是server001）
```bash
# 集群名字
brokerClusterName=myCluster   

# broker名字，集群中主节点需要唯一（注意：主从节点需要一致）
brokerName=broker-m1

# 0表示 Master，大于0表示 Slave
brokerId=0

# Broker对外服务绑定地址
brokerIP1=server001

# Broker对外服务端口
listenPort=10911

# nameServer地址，多个以分号(;)分割
namesrvAddr=server003:9876;server004:9876

#在发送消息时，自动创建服务器不存在的 topic，默认创建的队列数。建议线下开启，线上关闭（注意：消费者数量最好是这个数的倍数，以达到消息最好的负载均衡）
#defaultTopicQueueNums=4

# 是否允许 Broker 自动创建 Topic，建议线下开启，线上关闭
autoCreateTopicEnable=false

# 是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=false

# 删除文件时间点，默认凌晨 4 点
deleteWhen=04

# 文件保留时间，默认 48 小时
fileReservedTime=120

# commitLog 每个文件的大小默认 1G
mapedFileSizeCommitLog=1073741824

# ConsumeQueue 每个文件默认存 30W 条，根据业务情况调整
mapedFileSizeConsumeQueue=300000

#destroyMapedFileIntervalForcibly=120000
#redeleteHangedFileInterval=120000

# 检测物理文件磁盘空间，如果磁盘空间使用超过88%（注意：这里配置的是一个百分比）
diskMaxUsedSpaceRatio=88

# 数据存储路径（注意：需要手动创建目录）
storePathRootDir=/home/rocketmq-all-4.4.0-bin-release/data

# commitLog 存储路径（注意：需要手动创建目录）
storePathCommitLog=/home/rocketmq-all-4.4.0-bin-release/data/commitlog

# 消费队列存储路径存储路径（注意：需要手动创建目录）
storePathConsumeQueue=/home/rocketmq-all-4.4.0-bin-release/data/consumequeue

# 消息索引存储路径（注意：需要手动创建目录）
storePathIndex=/home/rocketmq-all-4.4.0-bin-release/data/index

# checkpoint 文件存储路径
storeCheckpoint=/home/rocketmq-all-4.4.0-bin-release/data/checkpoint

# abort 文件存储路径
abortFile=/home/rocketmq-all-4.4.0-bin-release/data/abort

# 消息消费失败，重试策略
#messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h

# 限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000

# Broker 的角色（注意：这个是配置主从节点数据复制模式的，生产建议使用同步复制）
#- ASYNC_MASTER 异步复制 Master（注意：这个选项，只能针对主节点）
#- SYNC_MASTER  同步复制 Master（注意：这个选项，只能针对主节点）
#- SLAVE        从节点（注意：这个选项，只能针对从节点）
brokerRole=SYNC_MASTER

# 数据刷盘方式（建议异步刷盘效率高，同步刷盘是要等数据落地磁盘后才返回客户端结果）
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH  同步刷盘
flushDiskType=ASYNC_FLUSH

#checkTransactionMessageEnable=false

# 发消息线程池数量
#sendMessageThreadPoolNums=128

# 拉消息线程池数量
#pullMessageThreadPoolNums=128
```

#### 五、修改[vi /home/rocketmq-all-4.4.0-bin-release/conf/my-cluater/broker-m2.conf]配置（注意：这是第二个主节点的配置，它对应的机器是server002）
```bash
# 集群名字
brokerClusterName=myCluster   

# broker名字，集群中主节点需要唯一（注意：主从节点需要一致）
brokerName=broker-m2

# 0表示 Master，大于0表示 Slave
brokerId=0

# Broker对外服务绑定地址
brokerIP1=server002

# Broker对外服务端口
listenPort=10911

# nameServer地址，多个以分号(;)分割
namesrvAddr=server003:9876;server004:9876

#在发送消息时，自动创建服务器不存在的 topic，默认创建的队列数。建议线下开启，线上关闭（注意：消费者数量最好是这个数的倍数，以达到消息最好的负载均衡）
#defaultTopicQueueNums=4

# 是否允许 Broker 自动创建 Topic，建议线下开启，线上关闭
autoCreateTopicEnable=false

# 是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=false

# 删除文件时间点，默认凌晨 4 点
deleteWhen=04

# 文件保留时间，默认 48 小时
fileReservedTime=120

# commitLog 每个文件的大小默认 1G
mapedFileSizeCommitLog=1073741824

# ConsumeQueue 每个文件默认存 30W 条，根据业务情况调整
mapedFileSizeConsumeQueue=300000

#destroyMapedFileIntervalForcibly=120000
#redeleteHangedFileInterval=120000

# 检测物理文件磁盘空间，如果磁盘空间使用超过88%（注意：这里配置的是一个百分比）
diskMaxUsedSpaceRatio=88

# 数据存储路径（注意：需要手动创建目录）
storePathRootDir=/home/rocketmq-all-4.4.0-bin-release/data

# commitLog 存储路径（注意：需要手动创建目录）
storePathCommitLog=/home/rocketmq-all-4.4.0-bin-release/data/commitlog

# 消费队列存储路径存储路径（注意：需要手动创建目录）
storePathConsumeQueue=/home/rocketmq-all-4.4.0-bin-release/data/consumequeue

# 消息索引存储路径（注意：需要手动创建目录）
storePathIndex=/home/rocketmq-all-4.4.0-bin-release/data/index

# checkpoint 文件存储路径
storeCheckpoint=/home/rocketmq-all-4.4.0-bin-release/data/checkpoint

# abort 文件存储路径
abortFile=/home/rocketmq-all-4.4.0-bin-release/data/abort

# 消息消费失败，重试策略
#messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h

# 限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000

# Broker 的角色（注意：这个是配置主从节点数据复制模式的，生产建议使用同步复制）
#- ASYNC_MASTER 异步复制 Master（注意：这个选项，只能针对主节点）
#- SYNC_MASTER  同步复制 Master（注意：这个选项，只能针对主节点）
#- SLAVE        从节点（注意：这个选项，只能针对从节点）
brokerRole=SYNC_MASTER

# 数据刷盘方式（建议异步刷盘效率高，同步刷盘是要等数据落地磁盘后才返回客户端结果）
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH  同步刷盘
flushDiskType=ASYNC_FLUSH

#checkTransactionMessageEnable=false

# 发消息线程池数量
#sendMessageThreadPoolNums=128

# 拉消息线程池数量
#pullMessageThreadPoolNums=128
```

#### 六、修改[vi /home/rocketmq-all-4.4.0-bin-release/conf/my-cluater/broker-m1-slave1.conf]配置（注意：这是第一个主节点的第一个从节点的配置，它对应的机器是server003）
```bash
# 集群名字
brokerClusterName=myCluster   

# broker名字，集群中主节点需要唯一（注意：主从节点需要一致）
brokerName=broker-m1

# 0表示 Master，大于0表示 Slave
brokerId=1

# Broker对外服务绑定地址
brokerIP1=server003

# Broker对外服务端口
listenPort=10911

# nameServer地址，多个以分号(;)分割
namesrvAddr=server003:9876;server004:9876

#在发送消息时，自动创建服务器不存在的 topic，默认创建的队列数。建议线下开启，线上关闭（注意：消费者数量最好是这个数的倍数，以达到消息最好的负载均衡）
#defaultTopicQueueNums=4

# 是否允许 Broker 自动创建 Topic，建议线下开启，线上关闭
autoCreateTopicEnable=false

# 是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=false

# 删除文件时间点，默认凌晨 4 点
deleteWhen=04

# 文件保留时间，默认 48 小时
fileReservedTime=120

# commitLog 每个文件的大小默认 1G
mapedFileSizeCommitLog=1073741824

# ConsumeQueue 每个文件默认存 30W 条，根据业务情况调整
mapedFileSizeConsumeQueue=300000

#destroyMapedFileIntervalForcibly=120000
#redeleteHangedFileInterval=120000

# 检测物理文件磁盘空间，如果磁盘空间使用超过88%（注意：这里配置的是一个百分比）
diskMaxUsedSpaceRatio=88

# 数据存储路径（注意：需要手动创建目录）
storePathRootDir=/home/rocketmq-all-4.4.0-bin-release/data

# commitLog 存储路径（注意：需要手动创建目录）
storePathCommitLog=/home/rocketmq-all-4.4.0-bin-release/data/commitlog

# 消费队列存储路径存储路径（注意：需要手动创建目录）
storePathConsumeQueue=/home/rocketmq-all-4.4.0-bin-release/data/consumequeue

# 消息索引存储路径（注意：需要手动创建目录）
storePathIndex=/home/rocketmq-all-4.4.0-bin-release/data/index

# checkpoint 文件存储路径
storeCheckpoint=/home/rocketmq-all-4.4.0-bin-release/data/checkpoint

# abort 文件存储路径
abortFile=/home/rocketmq-all-4.4.0-bin-release/data/abort

# 消息消费失败，重试策略
#messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h

# 限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000

# Broker 的角色（注意：这个是配置主从节点数据复制模式的，生产建议使用同步复制）
#- ASYNC_MASTER 异步复制 Master（注意：这个选项，只能针对主节点）
#- SYNC_MASTER  同步复制 Master（注意：这个选项，只能针对主节点）
#- SLAVE        从节点（注意：这个选项，只能针对从节点）
brokerRole=SLAVE

# 数据刷盘方式（建议异步刷盘效率高，同步刷盘是要等数据落地磁盘后才返回客户端结果）
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH  同步刷盘
flushDiskType=ASYNC_FLUSH

#checkTransactionMessageEnable=false

# 发消息线程池数量
#sendMessageThreadPoolNums=128

# 拉消息线程池数量
#pullMessageThreadPoolNums=128
```

#### 七、修改[vi /home/rocketmq-all-4.4.0-bin-release/conf/my-cluater/broker-m2-slave1.conf]配置（注意：这是第二个主节点的第一个从节点的配置，它对应的机器是server004）
```bash
# 集群名字
brokerClusterName=myCluster   

# broker名字，集群中主节点需要唯一（注意：主从节点需要一致）
brokerName=broker-m2

# 0表示 Master，大于0表示 Slave
brokerId=1

# Broker对外服务绑定地址
brokerIP1=server004

# Broker对外服务端口
listenPort=10911

# nameServer地址，多个以分号(;)分割
namesrvAddr=server003:9876;server004:9876

#在发送消息时，自动创建服务器不存在的 topic，默认创建的队列数。建议线下开启，线上关闭（注意：消费者数量最好是这个数的倍数，以达到消息最好的负载均衡）
#defaultTopicQueueNums=4

# 是否允许 Broker 自动创建 Topic，建议线下开启，线上关闭
autoCreateTopicEnable=false

# 是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=false

# 删除文件时间点，默认凌晨 4 点
deleteWhen=04

# 文件保留时间，默认 48 小时
fileReservedTime=120

# commitLog 每个文件的大小默认 1G
mapedFileSizeCommitLog=1073741824

# ConsumeQueue 每个文件默认存 30W 条，根据业务情况调整
mapedFileSizeConsumeQueue=300000

#destroyMapedFileIntervalForcibly=120000
#redeleteHangedFileInterval=120000

# 检测物理文件磁盘空间，如果磁盘空间使用超过88%（注意：这里配置的是一个百分比）
diskMaxUsedSpaceRatio=88

# 数据存储路径（注意：需要手动创建目录）
storePathRootDir=/home/rocketmq-all-4.4.0-bin-release/data

# commitLog 存储路径（注意：需要手动创建目录）
storePathCommitLog=/home/rocketmq-all-4.4.0-bin-release/data/commitlog

# 消费队列存储路径存储路径（注意：需要手动创建目录）
storePathConsumeQueue=/home/rocketmq-all-4.4.0-bin-release/data/consumequeue

# 消息索引存储路径（注意：需要手动创建目录）
storePathIndex=/home/rocketmq-all-4.4.0-bin-release/data/index

# checkpoint 文件存储路径
storeCheckpoint=/home/rocketmq-all-4.4.0-bin-release/data/checkpoint

# abort 文件存储路径
abortFile=/home/rocketmq-all-4.4.0-bin-release/data/abort

# 消息消费失败，重试策略
#messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h

# 限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000

# Broker 的角色（注意：这个是配置主从节点数据复制模式的，生产建议使用同步复制）
#- ASYNC_MASTER 异步复制 Master（注意：这个选项，只能针对主节点）
#- SYNC_MASTER  同步复制 Master（注意：这个选项，只能针对主节点）
#- SLAVE        从节点（注意：这个选项，只能针对从节点）
brokerRole=SLAVE

# 数据刷盘方式（建议异步刷盘效率高，同步刷盘是要等数据落地磁盘后才返回客户端结果）
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH  同步刷盘
flushDiskType=ASYNC_FLUSH

#checkTransactionMessageEnable=false

# 发消息线程池数量
#sendMessageThreadPoolNums=128

# 拉消息线程池数量
#pullMessageThreadPoolNums=128
```

#### 八、修改日志输出目录
```bash
# 进入到/home/rocketmq-all-4.4.0-bin-release/conf
$ cd /home/rocketmq-all-4.4.0-bin-release/conf

# 将XML文件里面包含${user.home}的内容替换成/home/rocketmq-all-4.4.0-bin-release
$ sed -i 's#${user.home}#/home/rocketmq-all-4.4.0-bin-release#g' *.xml
```

#### 九、修改[vi /home/rocketmq-all-4.4.0-bin-release/bin/runbroker.sh] RocketMQ Broker JVM启动参数（原因：默认配置内存过大，我们的机器可能没有那么大的内存或者是测试不需要那么大的内存，所以改小一点）
```bash
JAVA_OPT="${JAVA_OPT} -server -Xms768m -Xmx768m -Xmn256m"
```

#### 十、修改[vi /home/rocketmq-all-4.4.0-bin-release/bin/runserver.sh] RocketMQ NameServer JVM启动参数（原因：默认配置内存过大，我们的机器可能没有那么大的内存或者是测试不需要那么大的内存，所以改小一点）
```bash
JAVA_OPT="${JAVA_OPT} -server -Xms640m -Xmx640m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
```

#### 十一、分发RocketMQ安装文件到各个机器
```bash
$ scp -r /home/rocketmq-all-4.4.0-bin-release root@server002:/home/rocketmq-all-4.4.0-bin-release
$ scp -r /home/rocketmq-all-4.4.0-bin-release root@server003:/home/rocketmq-all-4.4.0-bin-release
$ scp -r /home/rocketmq-all-4.4.0-bin-release root@server004:/home/rocketmq-all-4.4.0-bin-release
```

#### 十二、启动NameServer服务（注意：server003和server004两台机器上都要执行）
```bash
$ cd /home/rocketmq-all-4.4.0-bin-release/bin
$ nohup ./mqnamesrv &  
```

#### 十三、启动RocketMQ Broker服务（注意：一定要到先到server003和server004上启动NameServer，因为Broker服务依赖NameServer存数元数据）
```bash
$ cd /home/rocketmq-all-4.4.0-bin-release/bin

# -c指定配置文件启动（注意：配置文件目录以当前命令执行目录为基准）；最后将命令启动时的输出信息放到/home/rocketmq-start.log文件  
# 到server001上，启动第一个Master节点      
$ nohup ./mqbroker -c ../conf/my-cluater/broker-m1.conf > /home/rocketmq-start.log 2>&1 &

# 到server002上，启动第二个Master节点      
$ nohup ./mqbroker -c ../conf/my-cluater/broker-m2.conf > /home/rocketmq-start.log 2>&1 &

# 到server003上，启动第一个Master节点的第一个Slave节点   
$ nohup ./mqbroker -c ../conf/my-cluater/broker-m1-slave1.conf > /home/rocketmq-start.log 2>&1 &

# 到server004上，启动第二个Master节点的第一个Slave节点   
$ nohup ./mqbroker -c ../conf/my-cluater/broker-m2-slave1.conf > /home/rocketmq-start.log 2>&1 &
```

#### 十四、关闭服务
```bash
$ cd /home/rocketmq-all-4.4.0-bin-release/bin

# 先关闭Broker服务
$ ./mqshutdown broker

# 再关闭NameServer服务
$ ./mqshutdown namesrv
```

#### 十五、RocketMQ控制台源码地址：https://github.com/apache/rocketmq-externals/tree/master/rocketmq-console 下载整个项目，然后将rocketmq-console模块导入到Eclipse使用即可（注意：不需要导入整个项目），也可以自己打包
