#!/bin/sh
#
# Execute Only Once 只可执行一次
#
#用户名
USER=admin
#磁盘盘符
DISK=sda
##在grub.conf中添加参数 默认注释掉 需要使用请取消注释
#sed -i 's/kernel.*$/& elevator=deadline/' /etc/grub.conf

echo 'vm.overcommit_memory=1' >> /etc/sysctl.conf
echo 'vm.min_free_kbytes=5000000' >> /etc/sysctl.conf
echo 'vm.drop_caches=1' >> /etc/sysctl.conf
echo 'vm.zone_reclaim_mode=0' >> /etc/sysctl.conf
echo 'vm.max_map_count=655360' >> /etc/sysctl.conf
echo 'vm.dirty_background_ratio=50' >> /etc/sysctl.conf
echo 'vm.dirty_ratio=50' >> /etc/sysctl.conf
echo 'vm.page-cluster=3' >> /etc/sysctl.conf
echo 'vm.dirty_writeback_centisecs=360000' >> /etc/sysctl.conf
echo 'vm.swappiness=10' >> /etc/sysctl.conf
sysctl -p
echo "ulimit -n 655350" >> /etc/profile
echo "$USER hard nofile 655350" >> /etc/security/limits.conf
echo 'deadline' > /sys/block/$DISK/queue/scheduler
echo "---------------------------------------------------------------"
sysctl vm.overcommit_memory
sysctl vm.min_free_kbytes
sysctl vm.drop_caches
sysctl vm.zone_reclaim_mode
sysctl vm.max_map_count
sysctl vm.dirty_background_ratio
sysctl vm.dirty_ratio
sysctl vm.page-cluster
sysctl vm.dirty_writeback_centisecs
sysctl vm.swappiness
su - $USER -c 'ulimit -n'
cat /sys/block/$DISK/queue/scheduler