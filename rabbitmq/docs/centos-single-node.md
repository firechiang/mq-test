#### 一、下载安装包（注意：RabbitMQ依赖Erlang环境，而且对版本有要求，具体版本对应请参照官网：https://www.rabbitmq.com/which-erlang.html）
```bash
$ cd /home/tools
$ wget https://packages.erlang-solutions.com/erlang/rpm/centos/7/x86_64/esl-erlang_22.0.1-1~centos~7_amd64.rpm
$ rpm -ivh esl-erlang_22.0.1-1~centos~7_amd64.rpm
$ wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.15/rabbitmq-server-generic-unix-3.7.15.tar.xz
$ tar -xvf rabbitmq-server-generic-unix-3.7.15.tar.xz -C ../           
```

