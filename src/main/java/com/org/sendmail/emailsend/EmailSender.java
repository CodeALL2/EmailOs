package com.org.sendmail.emailsend;

import com.org.sendmail.Util.InitSendEmailData;
import com.org.sendmail.mapper.EmailDataInfo;
import com.org.sendmail.mapper.EmailFailRepository;
import com.org.sendmail.mapper.EmailStatueRepository;
import com.org.sendmail.mapper.EmailTaskRepository;
import com.org.sendmail.model.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.Session;
import java.util.ArrayList;
import java.util.UUID;

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
    private EmailTaskRepository emailTaskRepository;
    private final Logger logger = LoggerFactory.getLogger(EmailSender.class);
    private EmailModel emailModel;
    private EmailDataInfo emailDataInfo;
    private EmailStatueRepository emailStatueRepository;
    private EmailFailRepository emailFailRepository;

    public EmailSender(EmailModel emailModel, EmailTaskRepository emailElasticSearchRepository, EmailDataInfo emailDataInfo, EmailStatueRepository emailStatueRepository, EmailFailRepository emailFailRepository){
        this.emailModel = emailModel;
        this.emailTaskRepository = emailElasticSearchRepository;
        this.emailDataInfo = emailDataInfo;
        this.emailStatueRepository = emailStatueRepository;
        this.emailFailRepository = emailFailRepository;
    }

    @Override
    public void run() {
        if (emailModel.getResendId() != null){
           //代表是重发包
            resendEmail();
            return;
        }

        logger.info("邮件正在发送中{}", emailModel.getOperationId());
        String host = emailModel.getHost();
        String authCode = emailModel.getAuthCode();
        //1. 发送者邮箱
        String senderEmail = emailModel.getSenderEmail();
        //2. 接受者邮箱
        ArrayList<String> accepterEmail = emailModel.getAccepterEmail();
        //3. 从es中捞出邮件
        EmailTask email = emailTaskRepository.findByTaskId(this.emailModel.getOperationId());
        if (email == null){
            log.error("{} 在es中未发现此邮件", emailModel.getOperationId());
            return;
        }
        String subject = email.getSubject();
        String text = email.getEmail_content();

        InitSendEmailData initSendEmailData = new InitSendEmailData(senderEmail, host, authCode);
        Session session = initSendEmailData.createSSLSocket();

        if (email.getTask_type() == 0 || email.getTask_type() == 2) { //普通邮件一次性发完
            for (int i = 0; i < accepterEmail.size(); i++) {
                log.info("接收者邮箱：{}", accepterEmail.get(i));
                UndeliveredEmail emailInfoData = initSendEmailData.sendEmail(subject, text, session, accepterEmail.get(i));
                emailInfoOut(emailInfoData, accepterEmail.get(i), emailModel.getAccepterName().get(i));
            }
        }else if(email.getTask_type() == 1){ //循环发送
            UndeliveredEmail emailInfoData = initSendEmailData.sendEmail(subject, text, session, accepterEmail.get(email.getIndex() - 1));
            emailInfoOut(emailInfoData, accepterEmail.get(email.getIndex() - 1), emailModel.getAccepterName().get(email.getIndex() - 1));
        }
        EmailStatue emailStatue = emailStatueRepository.findByTaskId(emailModel.getOperationId());
        emailStatue.setEmail_status(6); //标记已完成
        emailStatueRepository.save(emailStatue);
    }

    public void emailInfoOut(UndeliveredEmail emailInfoData, String acceptEmail, String acceptEmailName){
        Long nowTime = System.currentTimeMillis() / 1000;
        emailInfoData.setEmail_task_id(emailModel.getOperationId());
        // 生成一个随机的UUID
        String uuid = UUID.randomUUID().toString();
        emailInfoData.setEmail_id(uuid);
        emailInfoData.setSender_id(emailModel.getSenderEmail());
        emailInfoData.setReceiver_id(acceptEmail);
        emailInfoData.setSender_name(emailModel.getSenderName());
        emailInfoData.setReceiver_name(acceptEmailName);
        emailInfoData.setCreated_at(nowTime);
        emailInfoData.setUpdated_at(nowTime);
        emailInfoData.setStart_date(Long.valueOf(emailModel.getSendTime()));
        emailInfoData.setEnd_date(nowTime);
        emailDataInfo.save(emailInfoData);

        if (emailInfoData.getError_code().equals(500)){
            //插入到未送达的重发表中去
            EmailFail emailFail = new EmailFail();
            String id = UUID.randomUUID().toString();
            emailFail.setEmail_resend_id(id);
            emailFail.setAccepter_email(acceptEmail);
            emailFail.setStatue(0L);
            emailFail.setEmail_task_id(emailModel.getOperationId());
            emailFailRepository.save(emailFail);
        }
    }

    public void resendEmail(){
        String resendId = emailModel.getResendId();
        logger.info("重发包邮件正在发送中{}", resendId);
        String[] parts = resendId.split("\\|"); // 按竖线拆分
        resendId = parts[1];
        logger.info("重发包邮件id{}", resendId);
        //从数据库中取出接受者
        EmailFail emailFail = emailFailRepository.findById(resendId);

        if (emailFail == null){
            log.error("没有找到重发邮件");
            return;
        }
        String accepterEmail = emailFail.getAccepter_email();

        InitSendEmailData initSendEmailData = new InitSendEmailData(emailModel.getSenderEmail(), emailModel.getHost(), emailModel.getAuthCode());
        Session session = initSendEmailData.createSSLSocket();
        //3. 从es中捞出邮件
        EmailTask email = emailTaskRepository.findByTaskId(this.emailModel.getOperationId());
        UndeliveredEmail undeliveredEmail = initSendEmailData.sendEmail(email.getSubject(), email.getEmail_content(), session, accepterEmail);

        if (undeliveredEmail.getError_code().equals(200)){
            //代表重发成功
            //写入emailfail
            emailFail.setStatue(1L);
            emailFail.setEnd_time(System.currentTimeMillis() / 1000);
            emailFail.setError_msg("邮件重发成功");
            emailFailRepository.save(emailFail);
        }else{
            //代表重发失败
            //写入emailfail
            emailFail.setStatue(2L);
            emailFail.setEnd_time(System.currentTimeMillis() / 1000);
            emailFail.setError_msg(undeliveredEmail.getError_msg());
            emailFailRepository.save(emailFail);
        }


    }

}
