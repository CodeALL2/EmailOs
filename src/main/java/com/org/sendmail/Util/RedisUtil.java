package com.org.sendmail.Util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 往redis里面塞入一个数据
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value){
        boolean r = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            r = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return r;
    }

    /**
     * 从redis里面获取数据
     * @param key
     * @return
     */
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }
}
