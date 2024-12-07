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
        String text = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f4f4f4;\n" +
                "        }\n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            box-shadow: 0 4px 8px rgba(0,0,0,0.1);\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        .header {\n" +
                "            background: linear-gradient(135deg, #6a11cb, #2575fc);\n" +
                "            color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .header h1 {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 20px;\n" +
                "            color: #333333;\n" +
                "        }\n" +
                "        .content h2 {\n" +
                "            color: #6a11cb;\n" +
                "        }\n" +
                "        .content p {\n" +
                "            line-height: 1.6;\n" +
                "            margin: 10px 0;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            margin: 20px 0;\n" +
                "            padding: 10px 20px;\n" +
                "            background: #2575fc;\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            font-size: 16px;\n" +
                "            border-radius: 4px;\n" +
                "        }\n" +
                "        .button:hover {\n" +
                "            background: #1e63db;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            background: #f4f4f4;\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            color: #999999;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "        .footer a {\n" +
                "            color: #2575fc;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        .footer a:hover {\n" +
                "            text-decoration: underline;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <!-- Header -->\n" +
                "        <div class=\"header\">\n" +
                "            <h1>欢迎来到我们的平台！</h1>\n" +
                "        </div>\n" +
                "\n" +
                "        <!-- Content -->\n" +
                "        <div class=\"content\">\n" +
                "            <h2>亲爱的用户：</h2>\n" +
                "            <p>感谢您加入我们！我们非常期待能与您一起探索更多的可能性。</p>\n" +
                "            <p>点击下面的按钮开始体验：</p>\n" +
                "            <a href=\"https://example.com/start\" class=\"button\">立即开始</a>\n" +
                "            <p>如果您有任何疑问，请随时联系我们的支持团队。</p>\n" +
                "        </div>\n" +
                "\n" +
                "        <!-- Footer -->\n" +
                "        <div class=\"footer\">\n" +
                "            <p>您收到此邮件是因为您注册了我们的服务。</p>\n" +
                "            <p><a href=\"https://example.com/unsubscribe\">取消订阅</a> | <a href=\"https://example.com/privacy\">隐私政策</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
        long nowTime = System.currentTimeMillis() / 1000; //秒级
        EmailRedisModel emailRedisModel = new EmailRedisModel();
        emailRedisModel.setSenderEmail("codeboandly@163.com");
        emailRedisModel.setText(text);
        emailRedisModel.setSubject("测试");
        emailRedisModel.setTime(String.valueOf(nowTime));
        emailRedisModel.setType(2);
        emailRedisModel.setAccepterEmailList(new ArrayList<>(Arrays.asList("2653400637@qq.com", "codeboandly@163.com")));
        redisTemplate.opsForValue().set("email_1",  emailRedisModel);
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
