package com.org.sendmail.listener;

import com.alibaba.fastjson.JSON;
import com.org.sendmail.common.BufferQueue;
import com.org.sendmail.model.EmailModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "emailSendAndReceive",
        consumerGroup = "emailConsumer",
        consumeMode = ConsumeMode.CONCURRENTLY
)
public class EmailListener implements RocketMQListener<MessageExt> {

    private static final Logger logger = LoggerFactory.getLogger(EmailListener.class);
    @Override
    public void onMessage(MessageExt messageExt) {
        //1. 获取消息体
        byte[] body = messageExt.getBody();
        //2. 将字节流序列化成emailModel实体类
        String json = new String(body, StandardCharsets.UTF_8);
        EmailModel emailModel = JSON.parseObject(json, EmailModel.class);
        logger.info("Sending EmailModel to queue: {}", emailModel);
        //3. 将邮件投向缓冲队列
        BufferQueue.sendEmailToQueue(emailModel);
    }

}
