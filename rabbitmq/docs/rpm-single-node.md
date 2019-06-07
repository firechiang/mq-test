#### 一、下载安装包，我们使用 RabbitMQ-3.6.5-1 （注意：RabbitMQ依赖Erlang环境，而且对版本有要求，具体版本对应请[参照官网](https://www.rabbitmq.com/which-erlang.html)）
```bash
$ cd /home/tools
# 下载 Erlang 环境安装包
$ wget www.rabbitmq.com/releases/erlang/erlang-18.3-1.el7.centos.x86_64.rpm   
# 下载RabbitMQ-3.6.5-1 安装包             
$ wget www.rabbitmq.com/releases/rabbitmq-server/v3.6.5/rabbitmq-server-3.6.5-1.noarch.rpm 
```

#### 二、下载安装 Socat（注意：如果下面的地址下载不了，请使用[备用下载地址](https://github.com/firechiang/mq-test/raw/master/rabbitmq/data/socat-1.7.3.2-5.el7.lux.x86_64.rpm)），[编译 Socat 源码方式安装](https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/socat-src-install.md)
```bash
$ cd /home/tools
$ yum install tcp_wrappers
$ wget http://repo.iotti.biz/CentOS/7/x86_64/socat-1.7.3.2-5.el7.lux.x86_64.rpm # RabbitMQ 密钥安装包（这个可能下载不了）
$ rpm -ivh socat-1.7.3.2-5.el7.lux.x86_64.rpm
```

#### 三、安装 Erlang 环境
```bash
$ cd /home/tools
$ rpm -ivh erlang-18.3-1.el7.centos.x86_64.rpm                                  # 安装 Erlang
$ erl                                                                           # 如果进入了 Eshell 命令行，说明 Erlang 安装成功了      
$ halt().                                                                       # 退出 Eshell 命令行 
```

#### 四、安装 RabbitMQ
```bash
$ cd /home/tools
$ rpm -ivh rabbitmq-server-3.6.5-1.noarch.rpm                                   # 安装 RabbitMQ
```

#### 五、修改[vi /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.5/ebin/rabbit.app]
```bash
{loopback_users, [guest]}                                                       # 用户名和密码
```

#### 六、启动
```bash
$ service rabbitmq-server start                                                 # 启动  RabbitMQ 服务
$ service rabbitmq-server stop && epmd -kill                                    # 启动  RabbitMQ 服务并且停止 Erlang 守护进程
$ ps -ef | grep rabbit                                                          # 查看 RabbitMQ 进程信息
```

#### 七、安装插件（注意：安装插件时 RabbitMQ 必须是启动的否则无法安装）
```bash
# 查看 RabbitMQ 自带的插件列表
$ rabbitmq-plugins list                                                         

# 安装管理控制台插件，访问地址：http://192.168.229.133:15672，用户名和密码都是：guest，就是我们上面配置的
$ rabbitmq-plugins enable rabbitmq_management                                   
```
