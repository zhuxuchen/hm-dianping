package com.hmdp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Shop;
import com.hmdp.entity.User;
import com.hmdp.service.IUserService;
import com.hmdp.service.impl.ShopServiceImpl;

import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisIdWorker;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private ShopServiceImpl shopService;

    @Resource
    private CacheClient cacheClient;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ExecutorService es = Executors.newFixedThreadPool(500);
    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);

        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                System.out.println("id = " + id);
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - begin));
    }

    @Test
    void testSaveShop() throws InterruptedException {
//        shopService.saveShopToRedis(1L, 10L);
        Shop shop = shopService.getById(1L);
        cacheClient.setWithLogicalExpire(RedisConstants.CACHE_SHOP_KEY + 1L, shop, 10L, TimeUnit.SECONDS );
    }

    /**
     * 生成1000个用户的登录信息存入Redis
     */
    @Test
    void testLogin() {
        // 查询1000个用户信息
        List<User> userList = userService.query().last("limit 1000").list();
        for (User user : userList) {
            // 保存用户信息到redis
            // 随机生成token作为登录令牌
            String token = UUID.randomUUID().toString(true);
            // 将用户信息转成hashmap存储
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
            Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
            // 存储
            String tokenKey = LOGIN_USER_KEY + token;
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);
        }
    }

    /**
     * 将用户登录的token保存成文件放在项目根目录
     */
    @Test
    void saveTokens() throws IOException {
        Set<String> tokens = stringRedisTemplate.keys(LOGIN_USER_KEY + "*");
        System.out.println("tokens = " + tokens);
        @Cleanup FileWriter fileWriter = new FileWriter(System.getProperty("user.dir") + "\\tokens.txt");
        @Cleanup BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        assert tokens != null;
        for (String token : tokens) {
            String substring = token.substring(LOGIN_USER_KEY.length());
            String txt = substring + "\n";
            bufferedWriter.write(txt);
        }
    }

    /**
     * 保存秒杀库存到Redis中
     */
    @Test
    void addSeckillVoucher(){
        // 保存秒杀库存到Redis中
        stringRedisTemplate.opsForValue()
                .set(SECKILL_STOCK_KEY + 12, String.valueOf(200));
    }
}
