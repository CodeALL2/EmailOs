package com.org.emaildispatcher;

import com.org.emaildispatcher.centralcontrol.DecisionMaker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@Import(RocketMQAutoConfiguration.class)
@EnableScheduling
public class EmailDispatcherApplication implements CommandLineRunner {

    @Resource
    private DecisionMaker decisionMaker;

    public static void main(String[] args) {
        SpringApplication.run(EmailDispatcherApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        decisionMaker.run();
    }
}
