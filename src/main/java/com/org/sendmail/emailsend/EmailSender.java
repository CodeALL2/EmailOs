package com.org.sendmail.emailsend;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.org.sendmail.Util.InitSendEmailData;
import com.org.sendmail.Util.RedisUtil;
import com.org.sendmail.model.EmailModel;
import com.org.sendmail.model.EmailRedisModel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.Session;
import java.util.ArrayList;

/**
 * 这是一个邮件发送者
 *
 * <p>该类处理邮件的发送 选取相应的邮件服务器进行发送。</p>
 * @author cbs
 * @version 1.0
 * @since 2024-11-26
 */
@Slf4j
public class EmailSender implements Runnable{
    private RedisUtil redisUtil;
    private final Logger logger = LoggerFactory.getLogger(EmailSender.class);
    private EmailModel emailModel;

    public EmailSender(EmailModel emailModel, RedisUtil redisUtil){
        this.emailModel = emailModel;
        this.redisUtil = redisUtil;
    }

    @Override
    public void run() {
        logger.info("邮件正在发送中{}", emailModel);
        String host = emailModel.getHost();
        String authCode = emailModel.getAuthCode();
        //1. 发送者邮箱
        String senderEmail = emailModel.getSenderEmail();
        //2. 接受者邮箱
        ArrayList<String> accepterEmail = emailModel.getAccepterEmail();
        //3. 取出redis里面的key
        String redisKey = emailModel.getRedisKey();
        //4. 从redis中捞出邮件的全部信息
        Object value = redisUtil.get(redisKey);
        EmailRedisModel emailRedisModel = conversionToEmailRedisModel(value);

        if (emailRedisModel == null){
            log.error("{} 在redis中未发现此邮件", redisKey);
            return;
        }
        String subject = emailRedisModel.getSubject();
        String text = emailRedisModel.getText();

        InitSendEmailData initSendEmailData = new InitSendEmailData(senderEmail, host, authCode);
        Session session = initSendEmailData.createSSLSocket();

        for (String e : accepterEmail) {
            initSendEmailData.sendEmail(subject, text, session, e);
        }
    }

    private EmailRedisModel conversionToEmailRedisModel(Object value){
        String jsonString = JSON.toJSONString(value);
        EmailRedisModel emailRedisModel = JSONObject.parseObject(jsonString, EmailRedisModel.class);
        log.info("转换成功{}", emailRedisModel);
        return emailRedisModel;
    }

}
