package com.org.emaildispatcher.centralcontrol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.org.emaildispatcher.model.EmailModel;
import com.org.emaildispatcher.model.EmailRedisModel;
import com.org.emaildispatcher.model.EmailUser;
import com.org.emaildispatcher.util.ElasticSearchUtil;
import com.org.emaildispatcher.util.RedisUtil;
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

    private String redisKey;
    private RedisUtil redisUtil;
    private ElasticSearchUtil elasticSearchUtil;
    private RocketMQTemplate rocketMQ;

    public EmailProcessingUnit (String redisKey, RedisUtil redisUtil, RocketMQTemplate rocketMQ, ElasticSearchUtil elasticSearchUtil){
        this.redisKey = redisKey;
        this.redisUtil = redisUtil;
        this.rocketMQ = rocketMQ;
        this.elasticSearchUtil = elasticSearchUtil;
    }

    @Override
    public void run() {
        //从redis里面取出邮件任务
        EmailRedisModel emailTask = getRedisTask(redisKey);

        //1. 拿到发送者在elastic中的实体
        EmailUser emailUser = elasticSearchUtil.searchEmailsByEmailName("email_user", "name", emailTask.getSenderEmail());
        log.info("取出elasticsearch实体{}", emailUser);
        if (emailUser == null){
            //说明邮件有不完整的地方
            log.error("elastic表中没有发现此用户: {}", emailTask.getSenderEmail());
            return;
        }
        //获取当前时间
        long nowTime = System.currentTimeMillis() / 1000; //秒级
        //暂时 不管僵尸用户 往rocketmq队列里面塞任务
        //构建rocketmq中的消息体
        EmailModel emailModel = new EmailModel();
        //业务号，应该直接从数据库中取
        emailModel.setOperationId(3);
        emailModel.setRedisKey(redisKey);
        emailModel.setHost(emailUser.getHost());
        emailModel.setSenderEmail(emailUser.getEmail());
        emailModel.setAuthCode(emailUser.getAuthCode());
        emailModel.setAccepterEmail(new ArrayList<String>(emailTask.getAccepterEmailList()));
        emailModel.setSendTime(String.valueOf(nowTime));
        log.info("{}邮件已成功打包完毕", emailModel);

        String json = JSON.toJSONString(emailModel);
        SendResult result = rocketMQ.syncSend("emailSendAndReceive", json.getBytes(StandardCharsets.UTF_8));
        if (result.getSendStatus() == SendStatus.SEND_OK) {
            // 发送成功
            log.info("redisKey:{} 业务id: {} 时间: {}邮件任务投递到rocketmq成功", redisKey, 3, TimestampToDate.toTime(nowTime));
        }else {
            log.error("redisKey:{} 业务id: {} 时间: {}邮件任务投递到rocketmq失败", redisKey, 3, TimestampToDate.toTime(nowTime));
        }
    }

    public EmailRedisModel getRedisTask(String key){
        Object value = redisUtil.getEmailTask(key);
        String jsonString = JSON.toJSONString(value);
        EmailRedisModel emailRedisModel = JSONObject.parseObject(jsonString, EmailRedisModel.class);
        log.info("转换成功{}", emailRedisModel);
        return emailRedisModel;
    }

}
