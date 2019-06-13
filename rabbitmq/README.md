#### 一、RabbitMQ消息流程图
![object](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/rabbitmq-structure.svg)
#### 二、Virtual Host(虚拟主机)说明
```bash
2.1，虚拟主机，就是虚拟地址用于逻辑隔离，是最上层的消息路由(可以简单理解为MySQL的库)
2.2，一个Virtual Host(虚拟主机)里面可以有若干个Exchange(交换机)和Queue(队列)
2.3，同一个Virtual Host(虚拟主机)里面不能有相同的Exchange(交换机)和Queue(队列)
```

#### 三、Exchange(交换机)说明
##### 3.1，Type(类型)：direct、topic、fanout、headers
```bash
3.1.1，direct：所有发送到 Direct类型的Exchange(交换机)的消息被转发到RoutingKey中指定的Queue(队列)上。

3.1.2，topic：所有发送到 Topic类型的Exchange(交换机)的消息被转发到所有关心RoutingKey中指定Topic的Queue(队列)上。
Exchange(交换机)将RoutingKey和某个Topic进行模糊匹配，此时队列需要绑定一个Topic（注意：# 模糊配置多个词，* 模糊配置一个词）

3.1.3，fanout：不处理任何RoutingKey(路由键)，只需要简单的将队列绑定到交换机,发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。
注意：fanout类型的Exchange(交换机)转发消息是最快的，性能是最好的

3.1.4，headers：通过头信息进行路由。
```
##### 3.3，Durability：是否持久化
##### 3.4，Auto Delete：当最后一个绑定到Exchange(交换机)上的队列删除后，自动删除该Exchange(交换机)
##### 3.5，Internal：当前Exchange(交换机)是否用于RabbitMQ内部使用，默认为False
##### 3.6，Arguments：扩展参数，用于扩展AMQP协议自定义一些属性


#### [四、3.6.x-rpm(一件安装) 单节点搭建][1]
#### [五、3.7.x-binary(二进制包) 单节点搭建][2]
#### [六、3.6.x-rpm(一键安装) 集群搭建（镜像全量模式）][5]
#### [七、HAProxy + Keepalived 集群负载均衡][6]
#### [八、插件安装][3]
#### [九、命令行简单使用][4]

[1]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/rpm-single-node.md
[2]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/centos-single-node.md
[3]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/rabbitmq-plugins-install.md
[4]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/command-simple-use.md
[5]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/cluster-image-model.md
[6]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/haproxy_keepalived_loadbalan.md