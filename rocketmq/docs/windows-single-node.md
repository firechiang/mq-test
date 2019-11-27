#### 一、下载安装包 [RocketMQ-4.4.0](http://mirrors.tuna.tsinghua.edu.cn/apache/rocketmq/4.4.0/rocketmq-all-4.4.0-bin-release.zip)，[官方安装文档](http://rocketmq.apache.org/docs/quick-start)
#### 二、解压安装包到D盘
#### 三、修改[D:/rocketmq-all-4.4.0-bin-release/conf/broker.conf]配置文件
```bash
# 集群名字
brokerClusterName=myCluster   

# broker 名字，注意此处不同的配置文件填写的不一样
brokerName=myBroker

# 0表示 Master，大于0表示 Slave
brokerId=0

# Broker对外服务绑定地址
brokerIP1=localhost

# Broker对外服务端口
listenPort=10911

# nameServer地址，多个以分号(;)分割
namesrvAddr=localhost:9876

#在发送消息时，自动创建服务器不存在的 topic，默认创建的队列数（注意：消费者数量最好是这个数的倍数，以达到消息最好的负载均衡）
defaultTopicQueueNums=4

# 是否允许 Broker 自动创建 Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true

# 是否允许 Broker 自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true

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

# 检测物理文件磁盘空间
diskMaxUsedSpaceRatio=88

# 数据存储路径（注意：需要手动创建目录）
storePathRootDir=D:\\rocketmq-all-4.4.0-bin-release\\data

# commitLog 存储路径（注意：需要手动创建目录）
storePathCommitLog=D:\\rocketmq-all-4.4.0-bin-release\\commitlog

# 消费队列存储路径存储路径（注意：需要手动创建目录）
storePathConsumeQueue=D:\\rocketmq-all-4.4.0-bin-release\\consumequeue

# 消息索引存储路径（注意：需要手动创建目录）
storePathIndex=D:\\rocketmq-all-4.4.0-bin-release\\index

# checkpoint 文件存储路径
storeCheckpoint=D:\\rocketmq-all-4.4.0-bin-release\\checkpoint

# abort 文件存储路径
abortFile=D:\\rocketmq-all-4.4.0-bin-release\\abort

# 消息消费失败，重试策略
#messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h

# 限制的消息大小
maxMessageSize=65536
#flushCommitLogLeastPages=4
#flushConsumeQueueLeastPages=2
#flushCommitLogThoroughInterval=10000
#flushConsumeQueueThoroughInterval=60000

# Broker 的角色
#- ASYNC_MASTER 异步复制 Master
#- SYNC_MASTER 同步双写 Master
#- SLAVE
brokerRole=ASYNC_MASTER

# 刷盘方式
#- ASYNC_FLUSH 异步刷盘
#- SYNC_FLUSH 同步刷盘
flushDiskType=ASYNC_FLUSH

#checkTransactionMessageEnable=false

# 发消息线程池数量
#sendMessageThreadPoolNums=128

# 拉消息线程池数量
#pullMessageThreadPoolNums=128
```

#### 四、修改[D:/rocketmq-all-4.4.0-bin-release/bin/runbroker.cmd] RocketMQ Broker JVM启动参数（原因：默认配置内存过大，我们的机器可能没有那么大的内存或者是测试不需要那么大的内存，所以改小一点）
```bash
JAVA_OPT="${JAVA_OPT} -server -Xms768m -Xmx768m -Xmn256m"
```

#### 五、修改[D:/rocketmq-all-4.4.0-bin-release/bin/runserver.cmd] RocketMQ NameServer JVM启动参数（原因：默认配置内存过大，我们的机器可能没有那么大的内存或者是测试不需要那么大的内存，所以改小一点）
```bash
JAVA_OPT="${JAVA_OPT} -server -Xms640m -Xmx640m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
```
#### 六、配置 ROCKETMQ_HOME 环境变量，Value是RocketMQ所在目录（注意：只需要环境变量即可）

#### 七、启动服务（注意：NameServer相当于ZK的角色）
```bash
# 启动 NameSerevr
$ d: && cd rocketmq-all-4.4.0-bin-release/bin && mqnamesrv.cmd
             
# 启动 Broker（注意：重新开启CMD命令行窗口；-c指定配置文件启动（注意：配置文件目录以当前命令执行目录为基准））         
$ d: && cd rocketmq-all-4.4.0-bin-release/bin && mqbroker.cmd -c ../conf/broker.conf
```

#### 八、RocketMQ控制台源码地址：https://github.com/apache/rocketmq-externals/tree/master/rocketmq-console 下载整个项目，然后将rocketmq-console模块导入到Eclipse使用即可（注意：不需要导入整个项目），也可以自己打包

