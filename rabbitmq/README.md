#### 一、RabbitMQ消息流程图
![image](https://github.com/firechiang/mq-test/blob/master/rabbitmq/image/rabbitmq-structure.jpg)
#### 二、Exchange(交换机)属性说明
##### 2.1，Name(名称)
##### 2.2，Type(类型)：direct、topic、fanout、headers
```bash
2.2.1，direct：所有发送到 Direct类型的Exchange(交换机)的消息被转发到RoutingKey中指定的Queue(队列)上。

2.2.2，topic：所有发送到 Topic类型的Exchange(交换机)的消息被转发到所有关心RoutingKey中指定Topic的Queue(队列)上。
Exchange(交换机)将RoutingKey和某个Topic进行模糊匹配，此时队列需要绑定一个Topic（注意：# 模糊配置多个词，* 模糊配置一个词）

2.2.3，fanout：不处理任何RoutingKey(路由键)，只需要简单的将队列绑定到交换机,发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。
（注意：fanout类型的Exchange(交换机)转发消息是最快的，性能是最好的）

2.2.4，headers：通过头信息进行路由。
```
##### 2.3,Durability(是否持久化)
##### 2.4，Auto Delete：当最后一个绑定到Exchange(交换机)上的队列删除后，自动删除该Exchange(交换机)
##### 2.5,Internal：当前Exchange(交换机)是否用于RabbitMQ内部使用，默认为False
##### 2.6，Arguments：扩展参数，用于扩展AMQP协议自定义一些属性


#### [三、3.6.x-rpm(一件安装) 单节点搭建][1]
#### [四、3.7.x-binary(二进制包) 单节点搭建][2]
#### [五、插件安装][3]
#### [六、命令行简单使用][4]

[1]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/rpm-single-node.md
[2]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/centos-single-node.md
[3]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/rabbitmq-plugins-install.md
[4]: https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/command-simple-use.md