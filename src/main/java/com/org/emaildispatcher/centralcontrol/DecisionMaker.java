package com.org.emaildispatcher.centralcontrol;

import com.org.emaildispatcher.brige.BufferQueue;
import com.org.emaildispatcher.config.ElasticSearchConfig;
import com.org.emaildispatcher.model.BufferData;
import com.org.emaildispatcher.model.EmailRedisModel;
import com.org.emaildispatcher.util.ElasticSearchUtil;
import com.org.emaildispatcher.util.RedisUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 控制单元 邮件分发器
 */
@Component
@EnableAsync
@Slf4j
public class DecisionMaker {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RocketMQTemplate rocketMQ;
    @Resource
    private ThreadPoolTaskExecutor threadPool;
    @Resource
    private ElasticSearchUtil elasticSearchUtil;

    private final long SLEEP_TIME = 100;

    @Async
    public void run(){
        log.info("控制中心已启动");
        while (true){
            BufferData data = BufferQueue.getData();
            log.info("bufferQueue消息已到达控制中心:{}", data);
            String redisKey = data.getRedisKey();
//            //从redis里面取出邮件任务
//            EmailRedisModel emailTask = redisEmailTaskProvider.getRedisTask(reidKey);
//            //检查邮件是否完整
//            if (!EmailTaskChecker.checkEmail(emailTask)) {
//                //说明邮件有不完整的地方
//                log.error("{}邮件不完整", reidKey);
//                continue;
//            }

            while (true) {
                // 投递任务
                //   投递任务之前 判断一下选取的线程是否已经满载
                if (!threadPoolIsFull(threadPool)) {
                    //如果未满载 则投递任务
                    break;
                }else {
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        log.error("选取空闲线程池睡眠中断{}", e);
                    }
                }
            }
            //将邮件投递给线程池
            threadPool.execute(new EmailProcessingUnit(redisKey, redisUtil, rocketMQ, elasticSearchUtil));
        }
    }

    /**
     * 判断选取的线程池是否满载
     * @return
     */
    private boolean threadPoolIsFull(ThreadPoolTaskExecutor threadPool){
        if (threadPool.getActiveCount() >= threadPool.getMaxPoolSize() &&
                threadPool.getQueueSize() == threadPool.getQueueCapacity()){
            log.warn("线程池已满载");
            return true;
        }
        return false;
    }
}
