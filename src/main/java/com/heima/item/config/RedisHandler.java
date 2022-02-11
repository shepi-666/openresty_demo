package com.heima.item.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.item.pojo.Item;
import com.heima.item.pojo.ItemStock;
import com.heima.item.service.IItemService;
import com.heima.item.service.IItemStockService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class RedisHandler implements InitializingBean {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private IItemService itemService;

    @Resource
    private IItemStockService stockService;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化缓存
        // 1 查询商品信息
        List<Item> list = itemService.list();

        // 2 放入到缓存中
        for (Item item : list) {
            // 2.1 item序列化为JSON
            String json = MAPPER.writeValueAsString(item);
            // 2.2 存入redis中
            redisTemplate.opsForValue().set("item:id" + item.getId(), json);
        }

        // 1 查询商品库存信息
        List<ItemStock> stockList = stockService.list();

        // 2 放入到缓存中
        for (ItemStock stock : stockList) {
            // 2.1 item序列化为JSON
            String json = MAPPER.writeValueAsString(stock);
            // 2.2 存入redis中
            redisTemplate.opsForValue().set("stock:id" + stock.getId(), json);
        }
    }
}
