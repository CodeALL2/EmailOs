package com.org.sendmail;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.org.sendmail.Util.ElasticSearchUtil;
import com.org.sendmail.common.MqNameSrvAddrConfig;
import com.org.sendmail.model.EmailModel;

import com.org.sendmail.model.EmailRedisModel;
import com.org.sendmail.model.ShadowUser;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@SpringBootTest
class SendmailApplicationTests {

    @Test
    void contextLoads() throws Exception {
//        //创建一个生产者
//        DefaultMQProducer producer = new DefaultMQProducer("tes12t-producer-group");
//        //连接namesrv
//        producer.setNamesrvAddr(MqNameSrvAddrConfig.NAME_SRV_ADDR);
//        //启动
//        producer.start();
//        EmailModel emailModel = new EmailModel();
//        emailModel.setId(1);
//        emailModel.setType(1);
//        emailModel.setSenderType(2);
//        String json = JSON.toJSONString(emailModel);
//
//        //创建一个消息
//        Message message = new Message("emailSendAndReceive", json.getBytes(StandardCharsets.UTF_8));
//        //发送消息
//        for (int i = 0 ; i < 1; i++) {
//            SendResult send = producer.send(message);
//            System.out.println(send.getSendStatus() + " " + i);
//        }
//
//        //关闭生产者
//        producer.shutdown();
    }
    @Resource
    private ElasticSearchUtil esController;

    @Test
    public void testElasticSearch(){
        ShadowUser shadowUser = esController.searchEmails("email_shadow", "name", "2653400637@qq.com");
        System.out.println(shadowUser);
    }

    @Test
    public void testElasticSearchById(){
        ShadowUser emailShadow = esController.getDocumentById("email_shadow", "1");
        System.out.println(emailShadow);
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testSetEmailDataToRedis(){
        EmailRedisModel emailRedisModel = new EmailRedisModel();
        emailRedisModel.setSenderEmail("2653400637@qq.com");
        //emailRedisModel.setShadowList(new ArrayList<>(Arrays.asList()));
        emailRedisModel.setText("邮件内容");
        emailRedisModel.setSubject("邮件主题");
        //emailRedisModel.setShadowList(new ArrayList<>(Arrays.asList()));
        emailRedisModel.setAccepterEmailList(new ArrayList<>(Arrays.asList("2624565166@qq.com", "3439125957@qq.com")));
        redisTemplate.opsForValue().set("email_2",  emailRedisModel);
    }

    /**
     * bject jsonData =  redisUtil.get(fileUploadInfo.getMd5());
     *         String jsonString = JSON.toJSONString(jsonData);
     *         FileUploadInfo fileUploadInfoCache = JSONObject.parseObject(jsonString, FileUploadInfo.class);
     */

    @Test
    public void testGetEmailDataFromRedis(){
        Object o = redisTemplate.opsForValue().get("email_2");
        String jsonString = JSON.toJSONString(o);
        EmailRedisModel emailRedisModel = JSONObject.parseObject(jsonString, EmailRedisModel.class);
        System.out.println(emailRedisModel);
    }

}
