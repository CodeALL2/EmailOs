package com.org.sendmail.Util;


import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;


@Slf4j
public class InitSendEmailData {
    private static final Logger logger = LoggerFactory.getLogger(InitSendEmailData.class);
    private String senderEmail; //发送者邮箱
    //private String accepterEmail; //接受者邮箱
    private String host; //邮件服务器
    private String authCode; //用户邮箱授权码
    private static final Properties properties;

    static {
        properties = System.getProperties();
        properties.setProperty("mail.transport.protocol","smtp");
        properties.setProperty("mail.smtp.auth", "true");					// 开启认证
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
    }

    public InitSendEmailData(String senderEmail, String host, String authCode){
        this.senderEmail = senderEmail;
        this.host = host;
        this.authCode = authCode;
        properties.setProperty("mail.smtp.host", host); // 设置邮件服务器
    }

    public void sendEmail(String subject, String text, Session session, String accepterEmail){
        if (session == null){
            log.error("获取邮件SSL失败");
            return ;
        }
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(senderEmail));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(accepterEmail));        // Set To: 头部头字段
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(text, "text/html;charset=UTF-8");
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("{}邮件发送失败", accepterEmail);
            e.printStackTrace();
            return;
        }
        long nowTime = System.currentTimeMillis() / 1000; //秒级
        log.info("{} 邮件成功发送: {}->{}", senderEmail, accepterEmail, TimestampToDate.toTime(nowTime));
    }

    public Session createSSLSocket(){
        MailSSLSocketFactory mailSSLSocketFactory = null;
        Session session = null;
        try {
            mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
            //这里可以设计成往Redis里面塞 从redis里面取
            session = Session.getDefaultInstance(properties,new Authenticator(){
                public PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(senderEmail, authCode);	 	//发件人邮件用户名、授权码
                }
            });
        } catch (GeneralSecurityException e) {
            log.error("邮件SSL工厂初始化失败");
            e.printStackTrace();
        }
        return session;
    }



}
