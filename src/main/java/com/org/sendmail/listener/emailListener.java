package com.org.sendmail.listener;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "seckillTopic",
        consumerGroup = "seckill-consumer",
        consumeMode = ConsumeMode.CONCURRENTLY
)
public class emailListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        //接收消息队列中的邮件信息

    }
}
