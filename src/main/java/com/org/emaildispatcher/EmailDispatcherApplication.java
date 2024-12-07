package com.org.emaildispatcher;

import com.org.emaildispatcher.centralcontrol.DecisionMaker;
import com.org.emaildispatcher.netty.NettyServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.net.InetSocketAddress;

@Slf4j
@SpringBootApplication
@Import(RocketMQAutoConfiguration.class)
@EnableScheduling
public class EmailDispatcherApplication implements CommandLineRunner {

    @Resource
    private NettyServer nettyServer;
    @Resource
    private DecisionMaker decisionMaker;
    @Value("${nettyserver.ip}")
    private String nettyIp;
    @Value("${nettyserver.port}")
    private Integer nettyPort;

    public static void main(String[] args) {
        SpringApplication.run(EmailDispatcherApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        decisionMaker.run();
        InetSocketAddress address = new InetSocketAddress(nettyIp, nettyPort);
        log.info("Netty服务器已启动:{}:{}", nettyIp, nettyPort);
        nettyServer.start(address);
    }
}
