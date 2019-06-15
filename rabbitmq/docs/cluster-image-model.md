#### 一、集群节点分布（注意：集群各个节点都要配好主机名和IP）
```bash
-----------|----------|---------|------------------------|
           |  Master  |  Slave  |  HAProxy + Keepalived  |
-----------|----------|---------|------------------------|
server001  |     Y    |         |                        |
-----------|----------|---------|------------------------|
server002  |          |    Y    |                        |
-----------|----------|---------|------------------------|
server003  |          |    Y    |            Y           |
-----------|----------|---------|------------------------|
server004  |          |         |            Y           |
-----------|----------|---------|------------------------|
```
#### 二、下载安装包，我们使用 RabbitMQ-3.6.5-1 （注意：RabbitMQ依赖Erlang环境，而且对版本有要求，具体版本对应请[参照官网](https://www.rabbitmq.com/which-erlang.html)）
```bash
$ cd /home/tools

# 下载 Erlang 环境安装包
$ wget www.rabbitmq.com/releases/erlang/erlang-18.3-1.el7.centos.x86_64.rpm   

# 下载RabbitMQ-3.6.5-1 安装包             
$ wget www.rabbitmq.com/releases/rabbitmq-server/v3.6.5/rabbitmq-server-3.6.5-1.noarch.rpm 

# RabbitMQ 密钥安装包（注意：下载不了的话请使用备用地址：https://github.com/firechiang/mq-test/raw/master/rabbitmq/data/socat-1.7.3.2-5.el7.lux.x86_64.rpm）
# 如果想以编译 Socat 源码的方式安装：https://github.com/firechiang/mq-test/tree/master/rabbitmq/docs/socat-src-install.md
$ wget http://repo.iotti.biz/CentOS/7/x86_64/socat-1.7.3.2-5.el7.lux.x86_64.rpm 

# 下载 Socat 依赖项 tcp_wrappers
$ wget http://mirror.centos.org/centos/7/os/x86_64/Packages/tcp_wrappers-7.6-77.el7.x86_64.rpm
```

#### 三、分发安装包到集群各个节点
```bash
$ scp -r rabbitmq-server-3.6.5-1.noarch.rpm socat-1.7.3.2-5.el7.lux.x86_64.rpm tcp_wrappers-7.6-77.el7.x86_64.rpm erlang-18.3-1.el7.centos.x86_64.rpm root@server002:`pwd`
$ scp -r rabbitmq-server-3.6.5-1.noarch.rpm socat-1.7.3.2-5.el7.lux.x86_64.rpm tcp_wrappers-7.6-77.el7.x86_64.rpm erlang-18.3-1.el7.centos.x86_64.rpm root@server003:`pwd`
```

#### 四、集群各个节点安装 RabbitMQ-3.6.5-1
```bash
$ cd /home/tools
$ sudo rpm -ivh tcp_wrappers-7.6-77.el7.x86_64.rpm         # 安装 Socat 依赖 tcp_wrappers
$ sudo rpm -ivh socat-1.7.3.2-5.el7.lux.x86_64.rpm         # 安装 Socat 密钥包
$ sudo rpm -ivh erlang-18.3-1.el7.centos.x86_64.rpm        # 安装 Erlang

$ erl                                                      # 如果进入了 Eshell 命令行，说明 Erlang 安装成功了      
$ halt().                                                  # 退出 Eshell 命令行 

$ sudo rpm -ivh rabbitmq-server-3.6.5-1.noarch.rpm         # 安装 RabbitMQ
```

#### 五、修改[sudo vi /usr/lib/rabbitmq/lib/rabbitmq_server-3.6.5/ebin/rabbit.app]配置文件（注意：集群每个节点都要修改）
```bash
{loopback_users, [guest]}                                  # 用户名和密码
```

#### 六、启动集群的各个节点
```bash
$ sudo service rabbitmq-server start                       # 启动  RabbitMQ 服务
$ sudo service rabbitmq-server restart                     # 重启  RabbitMQ 服务
$ sudo service rabbitmq-server stop && epmd -kill          # 启动  RabbitMQ 服务并且停止 Erlang 守护进程
$ ps -ef | grep rabbit                                     # 查看 RabbitMQ 进程信息
```

#### 七、集群各个节点安装管理插件服务（注意：安装插件时 RabbitMQ 必须是启动的否则无法安装）
```bash
# 查看 RabbitMQ 自带的插件列表
$ rabbitmq-plugins list                                                         

# 安装管理控制台插件，访问地址：http://192.168.229.133:15672，用户名和密码都是：guest，就是我们上面配置的
$ rabbitmq-plugins enable rabbitmq_management                                   
```

#### 八、停止集群的各个节点
```bash
$ rabbitmqctl stop
```

#### 九、复制主节点的 .erlang.cookie 文件到集群各个节点（注意：以下操作要到主节点上执行）
```bash
$ cd /var/lib/rabbitmq                                     # 到 /var/lib/rabbitmq 目录
$ ll -a                                                    # 查看当前目录下所有文件夹以及文件
$ scp .erlang.cookie root@server002:`pwd`                  # 复制文件 .erlang.cookie 到 server002 的当前目录
$ scp .erlang.cookie root@server003:`pwd`                  # 复制文件 .erlang.cookie 到 server003 的当前目录
```

#### 十、以组建集群模式启动集群各个节点（注意：主节点先启动）
```bash
$ rabbitmq-server -detached                                # -detached 就是以组建集群模式启动
```

#### 十一、将集群所有的 Slave（从）节点加入到Master（主）主节点（注意：以下操作在Slave节点上执行，而且是集群所有的Slave节点都要执行）
```bash
# 停止当前Slave（从）节点 
$ rabbitmqctl stop_app                                     
# 将当前Slave（从）节点加入到 Master（主）节点  server001（注意：--disc 表示当前节点的数据以磁盘存储的方式；--ram 表示当前节点的数据以内存存储的方式）
$ rabbitmqctl join_cluster [--disc|--ram] rabbit@server001 
# 启动当Slave（从）从节点
$ rabbitmqctl start_app                                    
```

#### 十二、配置集群（注意：以下操作到集群任意节点执行都可以）
```bash
# 设置或修改集群的名称为  my_rabbitmq_cluster
$ rabbitmqctl set_cluster_name my_rabbitmq_cluster
# 设置集群以镜像全量模式复制队列数据到各个节点（就是一条消息，集群的每一个节点上都会有，也就是说这个模式不是切片模式）       
$ rabbitmqctl set_policy ha-all "^" '{"ha-mode":"all"}'
# 查看集群状态和节点情况
$ rabbitmqctl cluster_status                              
```

#### 十三、集群和RabbitMQ的基本操作
```bash
$ rabbitmqctl forget_cluster_node rabbit@server003         # 将 server003 节点移出集群
$ service rabbitmq-server start                            # 启动  RabbitMQ 服务
$ service rabbitmq-server restart                          # 重启  RabbitMQ 服务
$ service rabbitmq-server stop && epmd -kill               # 启动  RabbitMQ 服务并且停止 Erlang 守护进程
$ ps -ef | grep rabbit                                     # 查看 RabbitMQ 进程信息
```

