oms-seo是一个简单的分布式搜索引擎系统。索引采用Lucene去实现，并实现了一个简单的分布式索引同步的功能。

##### 相关配置

需要将有seo实例的ip配置在配置文件里面：

![image](https://img-blog.csdnimg.cn/20190215154421698.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)

同时需保证netty的端口号不被占用

##### 同步原理

分布式索引同步的原理如下图：

![image](https://img-blog.csdnimg.cn/20190215104643998.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)

下面是自测结果：

测试环境:三台服务器

192.168.109.3  本机windows

192.168.109.120  虚拟机centos7

192.168.109.121  虚拟机centos7

工程：

eureka注册中心，部署在192.168.109.3上。

zuul网关，部署在192.168.109.3上。

三个oms-seo项目实例，部署在三台机器上

mysql数据库，部署在192.168.109.121。

数据库表：


```
CREATE TABLE `T_INDEX_LOG`  (
  `ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'ip地址',
  `msg` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '存储的该操作的json报文',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `status` tinyint(4) NOT NULL COMMENT '0:  已插入的日志   1:待同步的日志',
  INDEX `IDX_IP`(`ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```


##### 1 分布式索引同步功能实现情况


在本机新增一个id为5的索引

![image](https://img-blog.csdnimg.cn/2019021511472683.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)

再查看其它机器上的结果：

![image](https://img-blog.csdnimg.cn/20190215115300794.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)

我就不一一贴三台机器了，查询结果都表示索引已同步。

##### 2 在服务器宕机的情况下

假如有服务器宕机时，应该做到其它存活机器依然正常查询，宕机机器重启之后，依然正常查询。

现在让192。168.109.3的服务宕机。在192.168.109.120的机器上执行一个插入操作，如下图：

![image](https://img-blog.csdnimg.cn/20190215145644453.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)

在121服务器上正常查询到：

![image](https://img-blog.csdnimg.cn/20190215150154106.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)

在192.168.109.3服务器重启后，依然查询到：

![image](https://img-blog.csdnimg.cn/2019021515055921.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)

注:在服务器重启之后数据同步会有最多一分钟（取决于定时器执行间隔）的延迟。

##### 3 在本地插入索引失败后，应该回滚之前插入的日志数据，确保一致性

在192。168.109.3进行测试，在页面上执行添加已有索引的操作，执行结果如下图：

![image](https://img-blog.csdnimg.cn/20190215152947894.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)

程序已抛出异常，此时查看数据库，并未插入15:31的日志数据，如下图：

![image](https://img-blog.csdnimg.cn/20190215153201902.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3pvcm9kdXl1,size_16,color_FFFFFF,t_70)