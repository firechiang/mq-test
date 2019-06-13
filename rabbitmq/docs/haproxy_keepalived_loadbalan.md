#### 一、下载源码和安装
```bash
$ yum install gcc
$ cd /home/tools
$ wget https://github.com/haproxy-unofficial-obsolete-mirrors/haproxy/archive/v1.7.0.tar.gz
$ wget http://www.haproxy.org/download/1.6/src/haproxy-1.6.5.tar.gz
$ tar -zxvf v1.7.0.tar.gz -C ./
$ cd haproxy-1.6.5
$ make TARGET=linux31 PREFIX=/usr/local/haproxy
$ make install PREFIX=/usr/local/haproxy

# 赋予权限
$ mkdir /etc/haproxy
$ groupadd -r -g 149 haproxy
$ useradd -g haproxy -r -s /sbin/nologin -u 149 haproxy


# 创建haproxy配置文件
$ touch /etc/haproxy/haproxy.cfg
$ vi /etc/haproxy/haproxy.cfg


#logging options
global
    log 127.0.0.1 local0 info
    maxconn 5120
    chroot /usr/local/haproxy
    uid 99
    gid 99
    daemon
    quiet
    nbproc 20
    pidfile /var/run/haproxy.pid
defaults
    log global
    # 使用4层代理模式，”mode http”为7层代理模式
    mode tcp
    # if you set mode to tcp,then you nust change tcplog into httplog
    option tcplog
    option dontlognull
    retries 3
    option redispatch
    maxconn 2000
    contimeout 5s
    # 客户端空闲超时时间为 60秒 则HA 发起重连机制
    clitimeout 60s
    # 服务器端链接超时时间为 15秒 则HA 发起重连机制
    srvtimeout 15s	
    # front-end IP for consumers and producters
listen rabbitmq_cluster
    bind 0.0.0.0:5672
    # 配置TCP模式
    mode tcp
    #balance url_paramuserid
    #balance url_paramsession_idcheck_post 64
    #balance hdr(User-Agent)
    #balance hdr(host)
    #balance hdr(Host) use_domain_only
    #balance rdp-cookie
    #balance leastconn
    #balance source //ip
    # 简单的轮询
    balance roundrobin
    # rabbitmq集群节点配置 #inter 每隔五秒对mq集群做健康检查， 2次正确证明服务器可用，2次失败证明服务器不可用，并且配置主备机制
    server server001 server001:5672 check inter 5000 rise 2 fall 2
    server server002 server002:5672 check inter 5000 rise 2 fall 2
    server server003 server003:5672 check inter 5000 rise 2 fall 2
#配置haproxy web监控，查看统计信息
listen stats
    bind server003:8100
    mode http
    option httplog
    stats enable
    # 设置haproxy监控地址为http://localhost:8100/rabbitmq-stats
    stats uri /rabbitmq-stats
    # 监控台 5s 刷新一次
    stats refresh 5s
```

```bash
4、启动haproxy
/usr/local/haproxy/sbin/haproxy -f /etc/haproxy/haproxy.cfg
//查看haproxy进程状态
ps -ef | grep haproxy




5、访问haproxy
PS:访问如下地址可以对rmq节点进行监控：http://192.168.1.27:8100/rabbitmq-stats





6、关闭haproxy
killallhaproxy
ps -ef | grep haproxy
```
