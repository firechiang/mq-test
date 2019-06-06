#### 一、编译安装 Erlang 环境，编译好的 Erlang 源码可以直接拷贝到其他机器使用 （注意：RabbitMQ依赖Erlang环境，而且对版本有要求，具体版本对应请[参照官网](https://www.rabbitmq.com/which-erlang.html)）
```bash
$ cd /home/tools
$ sudo yum install gcc glibc-devel make ncurses-devel openssl-devel autoconf # 安装编译工具（注意：编译时还需机器装有JDK）
$ wget http://erlang.org/download/otp_src_22.0.tar.gz                        # 下载源码包
$ tar -zxvf otp_src_22.0.tar.gz -C ./                                        # 解压到当前目录   
$ cd otp_src_22.0
$ ./configure && make && sudo make install                                   # 编译安装，默认安装在 /usr/local/lib 目录下

$ erl                                                                        # 如果进入了 Eshell 命令行，说明 Erlang 编译安装成功了      
$ halt().                                                                    # 退出 Eshell 命令行                                           
```

#### 二、下载安装 RabbitMQ，官方安装文档：https://www.rabbitmq.com/install-generic-unix.html
```bash
$ cd /home/tools
$ wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.15/rabbitmq-server-generic-unix-3.7.15.tar.xz
$ tar -xvf rabbitmq-server-generic-unix-3.7.15.tar.xz -C ../                 # 解压到上层目录
$ cd ../rabbitmq_server-3.7.15
```

#### 三、修改配置文件（注意：配置文件都需要手动创建），官方配置说明：https://www.rabbitmq.com/configure.html
##### 3.1、修改[vi /home/rabbitmq_server-3.7.15/etc/rabbitmq/rabbitmq-env.conf]配置，[官方配置说明](https://www.rabbitmq.com/configure.html#environment-env-file-unix)
```bash
# 覆盖节点名称
NODENAME=bunny@myhost   
# 指定传统配置文件位置  
CONFIG_FILE=/home/rabbitmq_server-3.7.15/etc/rabbitmq/rabbitmq.conf
# 指定高级配置文件位置（新版中某些配置设置不可用或难以使用sysctl格式进行配置。 因此，可以使用Erlang术语格式的其他配置文件（与rabbitmq.config相同）。 该文件通常名为advanced.config。 它将与rabbitmq.conf中提供的配置合并）
ADVANCED_CONFIG_FILE=/etc/rabbitmq/advanced.config
```

##### 3.2、修改[vi /home/rabbitmq_server-3.7.15/etc/rabbitmq/rabbitmq.conf]传统配置，[官方配置说明](https://www.rabbitmq.com/configure.html#config-items)，[官方配置示例](https://github.com/rabbitmq/rabbitmq-server/blob/v3.7.x/docs/rabbitmq.conf.example)
```bash
```

##### 3.3、修改[vi /home/rabbitmq_server-3.7.15/etc/rabbitmq/advanced.config]高级配置，[官官方配置示例](https://github.com/rabbitmq/rabbitmq-server/blob/master/docs/advanced.config.example)（注意：这个一般不需要配置）
```bash
```


