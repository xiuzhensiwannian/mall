<div align="center">
 <img src="https://img.shields.io/badge/license-MIT-blue">
 <img src="https://img.shields.io/badge/author-xiuzhensiwannian-green">
 <img src="https://img.shields.io/badge/language-java-red">
</div>

<div align="center">
  <img src="https://github.com/xiuzhensiwannian/mall/blob/master/mall.png"></center>
</div>

- [0 简介](#head1)
- [1 数据表初始化](#head2)
	- [1.1 user表中的唯一索引username与主键索引id](#head3)
	- [1.2 orderitem表中的普通索引](#head4)
	- [1.3 orderitem表的userid列的作用](#head5)

## <span id="head1">0 简介</span>
本项目基于慕课网电商项目-从零开始完成企业级java电商网站服务端开发 </br>
主要开发工具： </br>
IDE: IDEA </br>
JDK版本：jdk8 </br>
mysql：8.0.15 </br>
tomcat：9.0.27 </br>

## <span id="head2">1 数据表初始化</span>

### <span id="head3">1.1 user表中的唯一索引username与主键索引id</span>
用户注册时要确保username唯一，当有多个请求同时注册相同的用户名时，通过加锁实现，不过在项目演进（单一-分布式）时候会出现新的问题（Why？）。要确保某一列或多列唯一，一般使用主键primary key，而id作为user表的主键已经被使用了，此时可以使用mysql提供的唯一索引，它的作用是确保某一列或多列唯一，不过可以多次使用，即同一张表可以有多个唯一索引。

### <span id="head4">1.2 orderitem表中的普通索引</span>
orderitem表中有一个普通索引，orderno，这个索引可以加速查找orderno的速度
如果你还不知道什么是索引，请看这2篇文章

https://www.cnblogs.com/fengqiang626/archive/2019/09/04/11459434.html

https://www.cnblogs.com/jianmingyuan/p/6740090.html

