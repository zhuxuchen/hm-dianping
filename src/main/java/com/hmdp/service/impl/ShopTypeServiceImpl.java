package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Result listShopType() {
        // 1.从redis中查询商品缓存
        String key = SHOP_TYPE_KEY;
        String shopTypesJson = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopTypesJson)) {
            // 3.存在，直接返回
            List<ShopType> shopTypes = JSONUtil.toList(shopTypesJson, ShopType.class);
            return Result.ok(shopTypes);
        }
        // 4.不存在，根据id查询数据库
        List<ShopType> shopTypes = list();
        // 5.不存在，返回错误
        if (shopTypes == null) {
            return Result.fail("商品分类信息为空！");
        }
        // 6.存在，写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shopTypes));
        // 7.返回
        return Result.ok(shopTypes);
    }
}
