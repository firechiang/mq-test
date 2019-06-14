#### 一、下载源码和安装
```bash
$ cd /home/tools
$ sudo yum install gcc openssl-devel
# 备用下载地址：https://github.com/firechiang/mq-test/raw/master/rabbitmq/data/keepalived-2.0.16.tar.gz
$ wget https://www.keepalived.org/software/keepalived-2.0.16.tar.gz
$ tar -zxvf keepalived-2.0.16.tar.gz -C ./
$ cd keepalived-2.0.16
$ sudo ./configure --prefix=/usr/local/keepalived && sudo make && sudo make install
$ scp -r /home/tools/keepalived-2.0.16/keepalived/etc/init.d /usr/local/keepalived/etc
```

#### 二、修改[vi /usr/local/keepalived/etc/init.d/keepalived]开机启动脚本
```bash
# 将 /etc/sysconfig/keepalived 替换为如下内容（其实就是修改了 keepalived 系统配置文件所在目录）
/usr/local/keepalived/etc/sysconfig/keepalived
```

#### 三、备份默认配置文件
```bash
$ cp /usr/local/keepalived/etc/keepalived/keepalived.conf /usr/local/keepalived/etc/keepalived/keepalived1.conf
$ rm -f /usr/local/keepalived/etc/keepalived/keepalived.conf
```

#### 四、修改[vi /usr/local/keepalived/etc/keepalived/keepalived.conf]配置文件
```bash
! Configuration File for keepalived

global_defs {
  router_id server003                             ## 节点ID，通常为hostname（注意：主从节点不能一样）
}

# 配置脚本变量 chk_haproxy，每隔 2 秒执行一次 （注意：下面这个配置项需要手动创建）
vrrp_script chk_haproxy {
  script "/usr/local/keepalived/haproxy_check.sh" ## 执行检测的脚本所在位置位置
  interval 2                                      ## 检测时间间隔
  weight -20                                      ## 如果条件成立则权重减20
}

# 以下为具体节点的一些配置
vrrp_instance VI_1 {
  state MASTER                                    ## 主节点为MASTER，备份节点为BACKUP
  interface ens33                                 ## 绑定虚拟IP的网络接口（网卡），与本机IP地址所在的网络接口相同（Centos7默认：ens33）
  virtual_router_id 51                            ## 虚拟路由ID号，可以随便起（注意：主从节点需一致）
  priority 100                                    ## 优先级配置，越大优先级越高，主节点最好设置的比从节点大（0-254的值）
  advert_int 1                                    ## 节点间组播信息发送间隔，默认1s（注意：主从节点需一致）
  # 认证匹配
  authentication {
    auth_type PASS                                ## 密码类型
    auth_pass 1111                                ## 密码
  }
  # 配置要使用那些脚本变量（注意：下面这个配置项需要手动创建）
  track_script {
    chk_haproxy
  }
  # 虚拟IP要和真实IP的网段一致，可以指定多个（注意：主从节点需一致）
  virtual_ipaddress {
    192.168.229.16
    #192.168.229.17
    #192.168.229.18
  }
}
```

#### 五、创建[vi /usr/local/keepalived/haproxy_check.sh]检查 HAProxy 健康的脚本文件
```bash
#!/bin/bash
COUNT=`ps -C haproxy --no-header |wc -l`
if [ $COUNT -eq 0 ];then
    /usr/local/haproxy/sbin/haproxy -f /etc/haproxy/haproxy.cfg
    sleep 2
    if [ `ps -C haproxy --no-header |wc -l` -eq 0 ];then
        killall keepalived
    fi
fi

$ chmod +x /usr/local/keepalived/haproxy_check.sh   # 赋予脚本可执行权限
```

#### 六、分发安装包到从节点
```bash
$ scp -r /usr/local/keepalived root@server004:/usr/local
```

#### 七、修改[vi /usr/local/keepalived/etc/keepalived/keepalived.conf]从节点配置文件
```bash
global_defs {
  router_id server004                               ## 节点ID，通常为hostname（注意：主从节点不能一样）
}

# 以下为具体节点的一些配置
vrrp_instance VI_1 {
  state BACKUP                                      ## 主节点为MASTER，备份节点为BACKUP
  interface ens33                                   ## 绑定虚拟IP的网络接口（网卡），与本机IP地址所在的网络接口相同（Centos7默认：ens33）
  priority 90                                       ## 优先级配置，越大优先级越高，主节点最好设置的比从节点大（0-254的值）
}
```

#### 八、配置防火墙允许对应的IP访问（注意：每个节点都要配置）
```bash
$ sudo firewall-cmd --direct --permanent --add-rule ipv4 filter INPUT 0 --in-interface em1 --destination 192.168.229.132 --protocol vrrp -j ACCEPT
```

#### 九、修改 keepalived 配置文件和日志文件所在目录（注意：每个节点都要配置）
##### 9.1，修改[vi /usr/local/keepalived/etc/sysconfig/keepalived]
```bash
# -f 指定配置文件目录，-S 15 表示 local0.* 具体的还需要看一下/etc/rsyslog.conf文件
KEEPALIVED_OPTIONS="-f /usr/local/keepalived/etc/keepalived/keepalived.conf -D -S 15"
```
##### 9.2，修改[vi /etc/rsyslog.conf]添加如下内容
```bash
local0.*                                                /var/log/keepalived.log
```

#### 十、配置开机启动
```bash
$ sudo cp /usr/local/keepalived/etc/init.d/keepalived /etc/init.d/keepalived
```

#### 十一、启动和停止 keepalived
```bash
$ sudo chkconfig keepalived on                      # 开启 keepalived 开机启动
$ sudo chkconfig keepalived off                     # 关闭 keepalived开机启动
$ service keepalived start                          # 启动 
$ service keepalived restart                        # 重启
$ service keepalived stop                           # 停止
$ service keepalived status                         # 查看状态

$ ps -ef | grep keepalived                          # 查看 keepalived 进程信息
```
