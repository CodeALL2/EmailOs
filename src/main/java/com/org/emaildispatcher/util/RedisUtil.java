package com.org.emaildispatcher.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.org.emaildispatcher.model.EmailStatue;
import com.org.emaildispatcher.model.EmailUser;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Value("${server.port}")
    private String port;

    @Value("${timerQueue.name}")
    private String timerQueueName;


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

        redisTemplate.opsForZSet().add(timerQueueName + port, taskKey, executeTime);
    }

    public Set<Object> getTimerTask(long currentTime){
        System.out.println("定时任务队列的名字" + timerQueueName + port);
        return redisTemplate.opsForZSet().rangeByScore(timerQueueName + port, 0, currentTime);
    }

    public void deleteZSetKey(String key){
        redisTemplate.opsForZSet().remove(timerQueueName + port, key);
    }

    public void deleteObject(String key){
        redisTemplate.delete(key);
    }

//    public EmailRedisModel getRedisTask(String key){
//        Object value = this.getEmailTask(key);
//        String jsonString = JSON.toJSONString(value);
//        return JSONObject.parseObject(jsonString, EmailRedisModel.class);
//    }

//    public void setObject(String key, EmailRedisModel regularMail){
//        redisTemplate.opsForValue().set(key, regularMail);
//    }

    public Boolean lock(String lockName, String lockValue, int time){
        return redisTemplate.opsForValue().setIfAbsent(lockName, lockValue, 1, TimeUnit.MINUTES);
    }

    public void unlock(String lockName){
        redisTemplate.delete(lockName);
    }


}
