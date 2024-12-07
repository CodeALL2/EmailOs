package com.org.sendmail.emailsend;

import com.org.sendmail.Util.RedisUtil;
import com.org.sendmail.common.BufferQueue;
import com.org.sendmail.model.EmailModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 这是一个邮件分配器类，负责将邮件分发到不同的线程池中
 * @author cbs
 * @version 1.0
 * @since 2024-11-26
 */
@Component
public class EmailDispatcher {
    private final Logger logger = LoggerFactory.getLogger(EmailDispatcher.class);
    private final long SLEEP_TIME = 100;

    @Resource
    private ThreadPoolTaskExecutor threadPool;

    @Resource
    private RedisUtil redisUtil;

    @PostConstruct
    public void startEmailDispatcher(){
        Thread emailDispatcherThread = new Thread(this::emailDispatcherLoop);
        emailDispatcherThread.setDaemon(true);
        emailDispatcherThread.start();
    }

    private void emailDispatcherLoop(){
        logger.info("邮件分配器线程开启");

        while (true){
            //1. 从缓冲队列中取出邮件
            EmailModel email = BufferQueue.getEmailFromQueue();

            if (emailIsNull(email)){
                //1.1 如果邮件取出为空 则记录日志
                logger.error("邮件取出为空 及时排查emailQueue队列");
                continue;
            }

            //2. 邮件取出不为空
            //   基于最少连接算法 选择线程池任务最少的线程池 每次选择任务最少的线程池来执行。
            while (true) {
                //3. 投递任务
                //   投递任务之前 判断一下选取的线程是否已经满载
                if (!threadPoolIsFull(threadPool)) {
                    //3.1 如果未满载 则投递任务
                    break;
                }else {
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        logger.error("选取空闲线程池睡眠中断{}", e);
                    }
                }
            }

            //4.投递任务
            threadPool.execute(new EmailSender(email, redisUtil));
        }
    }

    /**
     * 判断从emailQueue缓冲队列中获取的邮件是否为空
     * @param emailModel
     * @return boolean
     */
    private boolean emailIsNull(EmailModel emailModel){
        return emailModel == null;
    }

    /**
     * 判断选取的线程池是否满载
     * @return
     */
    private boolean threadPoolIsFull(ThreadPoolTaskExecutor threadPool){
        if (threadPool.getActiveCount() >= threadPool.getMaxPoolSize() &&
            threadPool.getQueueSize() == threadPool.getQueueCapacity()){
            logger.warn("线程池已满载");
            return true;
        }
        return false;
    }



}
