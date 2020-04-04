# mall
本项目基于慕课网电商项目-从零开始完成企业级java电商网站服务端开发 </br>
主要开发工具： </br>
IDE: IDEA </br>
JDK版本：jdk8 </br>
mysql：8.0.15 </br>
tomcat：9.0.27 </br>

本文总结了电商项目中涉及到的java开发相关知识。一方面，在一刷时候囫囵吞枣视频很多地方没太搞懂，这次希望通过总结基础知识加深对项目理解。同时通过笔记的形式写出来，也可以加深对于基础知识的理解。最后系统的梳理项目也为了以后面试作准备。</br>
1 数据表初始化</br>

1.1 user表中的唯一索引username与主键索引id</br>
用户注册时要确保username唯一，当有多个请求同时注册相同的用户名时，通过加锁实现，不过在项目演进（单一-分布式）时候会出现新的问题（Why？）。要确保某一列或多列唯一，一般使用主键primary key，而id作为user表的主键已经被使用了，此时可以使用mysql提供的唯一索引，它的作用是确保某一列或多列唯一，不过可以多次使用，即同一张表可以有多个唯一索引。</br>

1.2 orderitem表中的普通索引</br>
orderitem表中有一个普通索引，orderno，这个索引可以加速查找orderno的速度
如果你还不知道什么是索引，请看这2篇文章，写的很好</br>
https://www.cnblogs.com/jianmingyuan/p/6740090.html</br>
https://www.cnblogs.com/fengqiang626/archive/2019/09/04/11459434.html</br>
可以看出，项目创建数据库表的过程已经把常见mysql索引都用到了，也明白了这三种索引的含义以及项目中的实际应用</br>

1.3 orderitem表的userid列的作用</br>
通过订单id到order表也可以查出user，不过需要花费时间，通过额外增加一列保存userid，可以提高sql查询效率

