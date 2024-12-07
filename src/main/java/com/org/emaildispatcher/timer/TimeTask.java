package com.org.emaildispatcher.timer;

import com.org.emaildispatcher.brige.BufferQueue;
import com.org.emaildispatcher.model.BufferData;
import com.org.emaildispatcher.util.RedisUtil;
import com.org.emaildispatcher.util.TimestampToDate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
@Slf4j
public class TimeTask {

    @Resource
    private RedisUtil redisUtil;

    @Scheduled(cron = "0 * * * * ?")
    public void executeTasks() {
        log.info("定时任务被执行");
        long nowTime = System.currentTimeMillis() / 1000;
        // 获取所有过期的任务（分数小于当前时间的任务）
        Set<Object> tasksToExecute = redisUtil.getTimerTask(nowTime);

        if (tasksToExecute != null){
            for (Object value : tasksToExecute){
                String task = (String)value;
                log.info("{}定时任务被取出 正在投递到执行队列 redisKey{}", task, TimestampToDate.toTime(nowTime));
                BufferQueue.putData(new BufferData(task));
                redisUtil.deleteZSetKey(task);
            }
        }
    }
}
