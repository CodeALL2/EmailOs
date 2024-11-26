package com.org.sendmail;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(RocketMQAutoConfiguration.class)
public class SendmailApplication {

    public static void main(String[] args) {
        SpringApplication.run(SendmailApplication.class, args);
    }

}
