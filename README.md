## 黑马点评项目

### 技术选型

+ 后端框架：
  + Spring
  + SpringMVC
  + Mybatis-Plus
  + MySQL
+ 前端框架：
  + Vue
  + ElementUI
+ 中间件：
  + Redis
  + Nginx

### 功能模块

本人主要完成后端部分的开发，实现功能如下：

- 短信登录
  - [x] 发送短信验证码
  - [x] 登录拦截
  - [x] 隐藏用户敏感信息
  - [x] 基于 Redis 实现共享 session 登录
  - [x] 登录状态刷新

- 商户查询缓存
  - [x] 添加商户缓存
  - [x] 解决商铺缓存与数据库的一致性[^1] 
  - [x] 解决缓存穿透问题[^2] ，缓存雪崩[^3]以及缓存击穿[^4]

- 优惠券秒杀
  - [x] Redis 实现全局唯一 id
  - [x] 添加优惠券
  - [x] 实现秒杀下单
  - [x] 乐观锁解决超卖问题
  - [x] 优惠券秒杀的一人一单
  - [x] 初步实现 Redis 分布式锁
  - [x] 解决分布式锁误删问题[^5]
  - [x] 利用 Lua 脚本解决多条命令的原子性问题
  - [x] 使用 Redisson 改造分布式锁
  - [x] 使用 stream 消息队列改造业务流程，实现异步秒杀下单

- 达人探店
  - [x] 发布探店笔记
  - [x] 查看探店笔记
  - [x] 点赞功能
  - [x] 点赞排行榜

- 好友关注
  - [x] 关注和取消关注
  - [x] 共同关注
  - [x] 使用 Feed 流推送动态到粉丝收件箱
  - [x] 好友动态的滚动分页查询

- 附近商户
  - [x] 导入店铺数据到 GEO
  - [x] 附近商品功能

- 好友签到
  - [x] 使用 BitMap 实现签到功能
  - [x] 签到统计

- UV 统计- HyperLogLog
  - [x] 测试百万数据的统计

### Q&A

首次启动项目时可能会遇到控制台报如下错

``` 
NOGROUP No such key 'stream.orders' or consumer group 'g1' in XREADGROUP with GROUP option
```

这是因为 SpringBoot 项目启动时会尝试访问 Redis，连接 Redis 中的 stream。请在启动 SpringBoot 项目前先在 Redis 中运行以下命令：

```
XGROUP CREATE stream.orders g1 $ MKSTREAM
```





[^1]: 使用先操作数据库，再删除缓存的策略保证数据库与缓存的一致性
[^2]: 在首次查询缓存未命中时，将空值写入 Redis
[^3]: 对热点数据添加随机 TTL 后缀
[^4]: 使用逻辑过期解决
[^5]: 存入线程标识到 Redis





