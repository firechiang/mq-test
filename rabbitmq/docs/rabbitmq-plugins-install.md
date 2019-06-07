#### 插件安装（注意：安装插件时 RabbitMQ 必须是启动的否则无法安装）
```bash
# 查看 RabbitMQ 自带的插件列表
$ rabbitmq-plugins list                                                         

# 安装管理控制台插件，访问地址：http://192.168.229.133:15672，用户名和密码都是：guest，就是我们上面配置的
$ rabbitmq-plugins enable rabbitmq_management                                   
```