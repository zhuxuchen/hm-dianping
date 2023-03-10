## 黑马点评项目

### 技术选型

+ 后端框架：
  + Spring
  + SpringMVC
  + Mybatis-Plus
+ 前端框架：
  + Vue
  + ElementUI
+ 数据库：
  + MySQL
+ 中间件：
  + Redis
  + Nginx

### 功能模块

本人主要完成后端部分的开发，实现功能如下：

- 短信登录
  - [x] 发送短信验证码
  - [x] 登录拦截
  - [x] 隐藏用户敏感信息
  - [x] 基于Redis实现共享session登录
  - [x] 登录状态刷新

- 商户查询缓存
  - [x] 添加商户缓存
  - [x] 解决商铺缓存与数据库的一致性[^1] 
  - [x] 解决缓存穿透问题[^2] ，缓存雪崩[^3]以及缓存击穿[^4]

- 优惠券秒杀
  - [x] Redis实现全局唯一id
  - [x] 添加优惠券
  - [x] 实现秒杀下单
  - [x] 乐观锁解决超卖问题
  - [x] 优惠券秒杀的一人一单
  - [x] 初步实现Redis分布式锁
  - [x] 解决分布式锁误删问题[^5 ]
  - [x] 利用Lua脚本解决多条命令的原子性问题
  - [x] 使用Redisson改造分布式锁
  - [x] 使用stream消息队列改造业务流程，实现异步秒杀下单

- 达人探店
  - [x] 发布探店笔记
  - [x] 查看探店笔记
  - [x] 点赞功能
  - [x] 点赞排行榜

- 好友关注
  - [x] 关注和取消关注
  - [x] 共同关注
  - [x] 使用Feed流推送动态到粉丝收件箱
  - [x] 好友动态的滚动分页查询

- 附近商户
  - [x] 导入店铺数据到GEO
  - [x] 附近商品功能

- 好友签到
  - [x] 使用BitMap实现签到功能
  - [x] 签到统计

- UV统计-HyperLogLog
  - [x] 测试百万数据的统计



[^1]: 使用先操作数据库，再删除缓存的策略保证数据库与缓存的一致性
[^2]: 在首次查询缓存未命中时，将空值写入Redis
[^3]: 对热点数据添加随机TTL后缀
[^4]: 使用逻辑过期解决
[^5]: 存入线程标识到Redis





