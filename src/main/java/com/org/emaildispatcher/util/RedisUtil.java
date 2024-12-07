package com.org.emaildispatcher.util;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisUtil {
    @Value("${server.port}")
    private String port;

    private final String timerQueue = "TIMER_TASK" + port;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public Object getEmailTask(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 添加定时任务
     * @param taskKey 任务在redis里面的key
     * @param executeTime 定时时间
     */
    public void addTimerTask(String taskKey, long executeTime){
        redisTemplate.opsForZSet().add(timerQueue, taskKey, executeTime);
    }

    public Set<Object> getTimerTask(long currentTime){
        return redisTemplate.opsForZSet().rangeByScore(timerQueue, 0, currentTime);
    }

    public void deleteZSetKey(String key){
        redisTemplate.opsForZSet().remove(timerQueue, key);
    }
}
