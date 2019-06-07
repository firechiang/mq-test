#### 一、安装插件（注意：安装插件时 RabbitMQ 必须是启动的否则无法安装）
```bash
# 查看 RabbitMQ 自带的插件列表
$ rabbitmq-plugins list                                                         

# 安装管理控制台插件，访问地址：http://192.168.229.133:15672，用户名和密码都是：guest，就是我们上面配置的
$ rabbitmq-plugins enable rabbitmq_management                                   
```

#### 二、命令行简单使用（角色说明：management=普通管理者，policymaker=策略制定者，monitoring=监控者，administrator=超级管理员）
```bash
$ rabbitmqctl status                                 # 查看节点状态
$ rabbitmqctl list_users                             # 查看所有用
$ rabbitmqctl add_user jiang jiang                   # 添加用户 jiang，密码 jiang
$ rabbitmqctl delete_user jiang                      # 删除用户jiang
$ rabbitmqctl change_password jiang a                # 修改用户jiang的密码为 a

$ rabbitmqctl set_user_tags jiang management         # 为用户 jiang 设置一个"普通管理者"的角色

$ rabbitmqctl add_vhost test_vhost                   # 添加一个虚拟主机，名字叫 test_vhost
$ rabbitmqctl delete_vhost test_vhost                # 删除虚拟主机 test_vhost
$ rabbitmqctl list_vhosts                            # 查看虚拟主机列表

# 使用户 jiang 具有 test_vhost 这个虚拟主机中所有资源的配置、写、读权限以便管理其中的资源
$ rabbitmqctl set_permissions -p test_vhost jiang ".*" ".*" ".*" 
$ rabbitmqctl list_user_permissions jiang            # 查看用户 jiang 的所有权限
$ rabbitmqctl list_permissions -p test_vhost         # 查看 test_vhost 虚拟主机中所有用户的权限
$ rabbitmqctl clear_permissions -p test_vhost jiang  # 清除用户 jiang 对虚拟主机 test_vhost 的所有权限权限

$ rabbitmqctl reset                                  # 移除 RabbitMQ 所有数据，建议在集群或节点关闭的情况下执行
$ rabbitmqctl list_queues                            # 查看所有队列信息
$ rabbitmqctl -p test_vhost purge_queue test_queue   # 清除虚拟主机 test_vhost 里面的 test_queue 队列里面的所有消息

# 将 server001,server002 组成集群（--disc=数据存储到磁盘，--ram=数据存储到内存）
$ rabbitmqctl join_cluster server001,server002 [--disc|--ram]
# 修改集群节点名称
$ rabbitmqctl rename_cluster_node oldNodeName newNodeName

$ rabbitmqctl cluster_status                         # 查看集群状态
$ rabbitmqctl change_cluster_node_type disc | ram    # 修改集群节点的存储模式（disc：存储到磁盘，ram：存储到内存）
$ rabbitmqctl forget_cluster_node [--offline]        # 忘记节点，就是将某个节点退出集群（摘除节点）
```