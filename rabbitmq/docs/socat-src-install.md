```bash
$ yum install gcc tcp_wrappers tcp_wrappers-devel readline-devel openssl-devel  # 安装编译依赖
$ wget http://www.dest-unreach.org/socat/download/socat-1.7.3.2.tar.gz          # 下载源码
$ tar -zxvf socat-1.7.3.2.tar.gz -C ./                                          # 解压到当前目录
$ cd socat-1.7.3.2                     
$ ./configure && make && make install                                           # 安装
$ socat -V                                                                      # 查看版本，验证是否安装成功
```
