#### 一、RingBuffer环形缓冲区（注意：缓冲区大小最好是2的N次方，更有利于基于二进制的计算机进行计算）
![object](https://github.com/firechiang/mq-test/blob/master/disruptor/image/ring.svg)
#### 二、RingBuffer（环形缓冲区）的占位算法是每来一个元素Count计数器加1，元素存储位置计算公式：Count计数器 % 缓冲区大小 = 存储位置
```bash
# 缓存区的大小
int size = 8;
第0个来的数据存储位置：(0 % size) = 0
第1个来的数据存储位置：(1 % size) = 1
第2个来的数据存储位置：(2 % size) = 2
第3个来的数据存储位置：(3 % size) = 3
第4个来的数据存储位置：(4 % size) = 4
第5个来的数据存储位置：(5 % size) = 5
第6个来的数据存储位置：(6 % size) = 6
第7个来的数据存储位置：(7 % size) = 7
第8个来的数据存储位置：(8 % size) = 0
第9个来的数据存储位置：(9 % size) = 1
第10个来的数据存储位置：(10 % size) = 2
第11个来的数据存储位置：(11 % size) = 3
```