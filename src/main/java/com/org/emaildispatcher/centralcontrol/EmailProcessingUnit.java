package com.org.emaildispatcher.centralcontrol;

import com.alibaba.fastjson.JSON;
import com.org.emaildispatcher.mapper.EmailFailRepository;
import com.org.emaildispatcher.mapper.EmailTaskRepository;
import com.org.emaildispatcher.mapper.EmailUserRepository;
import com.org.emaildispatcher.model.EmailFail;
import com.org.emaildispatcher.model.EmailModel;
import com.org.emaildispatcher.model.EmailTask;
import com.org.emaildispatcher.model.EmailUser;
import com.org.emaildispatcher.util.TimestampToDate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
/**
 * 邮件处理单元
 */
@Slf4j
public class EmailProcessingUnit implements Runnable{

    private String emailId;
    private EmailTaskRepository elasticSearchUtil;
    private RocketMQTemplate rocketMQ;
    private EmailUserRepository emailUserRepository;
    private EmailFailRepository emailFailRepository;

    public EmailProcessingUnit (String emailId, RocketMQTemplate rocketMQ, EmailTaskRepository elasticSearchUtil, EmailUserRepository emailUserRepository, EmailFailRepository emailFailRepository){
        this.emailId = emailId;
        this.rocketMQ = rocketMQ;
        this.elasticSearchUtil = elasticSearchUtil;
        this.emailUserRepository = emailUserRepository;
        this.emailFailRepository = emailFailRepository;
    }

    /**
     * 将邮件投送到rocketmq
     */
    @Override
    public void run() {
        //构建rocketmq中的消息体
        EmailModel emailModelRocketMq = new EmailModel();

        String[] parts = emailId.split("\\|"); // 按竖线拆分
        if (parts[0].equals("resend")){
            //代表是重发的包
            emailModelRocketMq.setResendId(emailId);
            emailId = parts[1];
            log.info("重发包id:{}", emailId);
            EmailFail emailFail = emailFailRepository.findById(emailId);
            if (emailFail == null){
                log.error("重发包id不存在");
                return;
            }
            emailId = emailFail.getEmail_task_id();
            emailModelRocketMq.setSendTime(String.valueOf(System.currentTimeMillis() / 1000));
            pushToRocket(emailModelRocketMq);
            return;
        }
        pushToRocket(emailModelRocketMq);
    }

    private void pushToRocket(EmailModel emailModelRocketMq){
        EmailTask emailModel = elasticSearchUtil.findByTaskId(emailId);
        log.info("控制中心消息实体{}", emailModel.getEmail_task_id());
        //1. 拿到发送者在elastic中的实体
        EmailUser emailUser = emailUserRepository.findByEmail(emailModel.getSender_id());
        log.info("取出发送者elasticsearch实体{}", emailUser);
        if (emailUser == null){
            //说明邮件有不完整的地方
            log.error("elastic表中没有发现此用户: {}", emailModel.getSender_id());
            return;
        }
        //获取当前时间
        long nowTime = System.currentTimeMillis() / 1000; //秒级
        //暂时 不管僵尸用户 往rocketmq队列里面塞任务

        emailModelRocketMq.setOperationId(emailModel.getEmail_task_id());
        emailModelRocketMq.setRedisKey(String.valueOf(emailModel.getEmail_task_id()));
        emailModelRocketMq.setHost(emailUser.getUser_host());
        emailModelRocketMq.setSenderEmail(emailUser.getUser_email());
        emailModelRocketMq.setSenderName(emailModel.getSender_name());
        emailModelRocketMq.setAuthCode(emailUser.getUser_email_code());
        emailModelRocketMq.setAccepterEmail(new ArrayList<String>(emailModel.getReceiver_id()));
        emailModelRocketMq.setSendTime(String.valueOf(nowTime));
        emailModelRocketMq.setAccepterName(emailModel.getReceiver_name());
        log.info("{}邮件已成功打包完毕", emailModelRocketMq);

        String json = JSON.toJSONString(emailModelRocketMq);
        log.info("json体{}:", json);
        SendResult result = rocketMQ.syncSend("emailSendAndReceive", json.getBytes(StandardCharsets.UTF_8));
        if (result.getSendStatus() == SendStatus.SEND_OK) {
            // 发送成功
            log.info("redisKey:{} 业务id: {} 时间: {}邮件任务投递到rocketmq成功", emailId, 3, TimestampToDate.toTime(nowTime));
        }else {
            log.error("redisKey:{} 业务id: {} 时间: {}邮件任务投递到rocketmq失败", emailId, 3, TimestampToDate.toTime(nowTime));
        }
    }
}
