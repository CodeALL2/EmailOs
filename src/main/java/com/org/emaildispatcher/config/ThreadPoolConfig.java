package com.org.emaildispatcher.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Value("${thread-pool.core-size}")
    private int coreSize;
    @Value("${thread-pool.max-size}")
    private int maxSize;

    @Value("${thread-pool.queue-capacity}")
    private int queueCapacity;

    @Value("${thread-pool.keep-alive-time}")
    private int keepAliveTime;

    @Value("${thread-pool.thread-name-prefix}")
    private String threadNamePrefix;


    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutorConfig(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置线程池的参数
        executor.setCorePoolSize(coreSize);  // 核心线程数
        executor.setMaxPoolSize(maxSize);  // 最大线程数
        executor.setQueueCapacity(queueCapacity); // 队列容量
        executor.setKeepAliveSeconds(keepAliveTime); // 设置线程空闲时间
        executor.setThreadNamePrefix(threadNamePrefix); // 设置线程池前缀名
        executor.initialize();  // 初始化线程池

        return executor;
    }
}
