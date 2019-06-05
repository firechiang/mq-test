#### 一、下载安装包（注意：RabbitMQ依赖Erlang环境，而且对版本有要求，具体版本对应请参照官网：https://www.rabbitmq.com/which-erlang.html）
```bash
$ cd /home/tools
$ wget https://packages.erlang-solutions.com/erlang/rpm/centos/7/x86_64/esl-erlang_22.0.1-1~centos~7_amd64.rpm
$ rpm -ivh esl-erlang_22.0.1-1~centos~7_amd64.rpm
$ wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.15/rabbitmq-server-3.7.15-1.el7.noarch.rpm
$ tar -xvf rabbitmq-server-generic-unix-3.7.15.tar.xz -C ../           
```


wget http://erlang.org/download/otp_src_22.0.tar.gz
sudo yum install -y gcc
wget http://www.unixodbc.org/unixODBC-2.3.7.tar.gz
tar -zxvf unixODBC-2.3.7.tar.gz
cd unixODBC-2.3.7
sudo ./configure --prefix=/usr/local/unixODBC
sudo make & make install





sudo yum install gcc glibc-devel make ncurses-devel openssl-devel autoconf

wget http://erlang.org/download/otp_src_R15B03.tar.gz

tar zxvf otp_src_R15B03.tar.gz

cd otp_src_R15B03

./configure && make && sudo make install







wget http://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
 rpm -ivh epel-release-latest-7.noarch.rpm

 sudo yum install erlang erlang-nox

