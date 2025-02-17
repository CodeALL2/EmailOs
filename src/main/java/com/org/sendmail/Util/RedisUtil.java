package com.org.sendmail.Util;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    @Value("${server.port}")
    private String port;

    @Value("${timerQueue.name}")
    private String timerQueueName;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private String emailPausedCache = "EMAIL_PAUSED_CACHE"; //邮件被暂停 一级缓存

    public void addTimerTask(String taskKey, long executeTime){
        System.out.println("队列的名字:" + timerQueueName + 9001);
        redisTemplate.opsForZSet().add(timerQueueName + 9001, taskKey, executeTime);
    }

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

    public void deleteObject(String key){
        redisTemplate.delete(key);
    }

    /**
     * 根据 key 删除 Set 集合中的某个元素
     * @param pausedEmailKey 要删除的邮件 key
     */
    public void removeEmailFromSet(String pausedEmailKey) {
        redisTemplate.opsForSet().remove(emailPausedCache, pausedEmailKey);
    }



}
