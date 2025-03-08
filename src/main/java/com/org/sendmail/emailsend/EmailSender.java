package com.org.sendmail.emailsend;

import com.org.sendmail.Util.InitSendEmailData;
import com.org.sendmail.mapper.*;
import com.org.sendmail.model.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private EmailSupplierRepository emailSupplierRepository;
    private EmailCustomerRepository emailCustomerRepository;
    private EmailReportRepository emailReportRepository;
    private EmailCountryRepository emailCountryRepository;
    private EmailContentRepository emailContentRepository;

    private static ArrayList<String> replaceContext = new ArrayList<>();



    static {
        replaceContext.add("联系人");
        replaceContext.add("联系人方式");
        replaceContext.add("收件人姓名");
        replaceContext.add("收件人国家");
        replaceContext.add("性别");
        replaceContext.add("出生日期");
        replaceContext.add("emailTaskId");
        replaceContext.add("receiverEmail");
    }

    public EmailSender(EmailModel emailModel, EmailTaskRepository emailElasticSearchRepository, EmailDataInfo emailDataInfo,
                       EmailStatueRepository emailStatueRepository, EmailFailRepository emailFailRepository,
                       EmailSupplierRepository emailSupplierRepository, EmailCustomerRepository emailCustomerRepository,
                       EmailReportRepository emailReportRepository, EmailCountryRepository emailCountryRepository,
                       EmailContentRepository emailContentRepository
    ){
        this.emailModel = emailModel;
        this.emailTaskRepository = emailElasticSearchRepository;
        this.emailDataInfo = emailDataInfo;
        this.emailStatueRepository = emailStatueRepository;
        this.emailFailRepository = emailFailRepository;
        this.emailSupplierRepository = emailSupplierRepository;
        this.emailCustomerRepository = emailCustomerRepository;
        this.emailReportRepository = emailReportRepository;
        this.emailCountryRepository = emailCountryRepository;
        this.emailContentRepository = emailContentRepository;
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
        String text;
        String emailContent = emailContentRepository.findContentById(emailModel.getOperationId());

        //替换文本
        HashMap<String, String> replaceMap = new HashMap<>();

        System.out.println("authCode=" + authCode  +" host=" + host +"  senderEmail=" + senderEmail);
        InitSendEmailData initSendEmailData = new InitSendEmailData(senderEmail, host, authCode);
        Session session = initSendEmailData.createSSLSocket();

        EmailCustomer emailCustomer;
        EmailSupplier emailSupplier;
        int  deliveryAmount = 0;
        int  bounce_amount = 0;

        if (email.getTask_type() == 1 || email.getTask_type() == 3) { //普通邮件一次性发完
            for (int i = 0; i < accepterEmail.size(); i++) {
                text = emailContent;
                //查询
                if ((emailCustomer = emailCustomerRepository.findByEmail(accepterEmail.get(i))) != null){
                    System.out.println("收件人信息替换curstomer" + emailCustomer.toString());
                    replacePrivateContext(replaceMap, emailCustomer.getCustomer_name(), emailCustomer.getSex(),emailCustomer.getBirth(), emailCustomer.getCustomer_country_id(), email.getEmail_task_id(), accepterEmail.get(i), emailCustomer.getContact_person(), emailCustomer.getContact_way());

                }else if((emailSupplier = emailSupplierRepository.findByEmail(accepterEmail.get(i))) != null){
                    System.out.println("收件人信息替换supper" + emailSupplier.toString());
                    replacePrivateContext(replaceMap, emailSupplier.getSupplier_name(), emailSupplier.getSex(), emailSupplier.getBirth(), emailSupplier.getSupplier_country_id(),email.getEmail_task_id(), accepterEmail.get(i), emailSupplier.getContact_person(), emailSupplier.getContact_way());
                }
                text = replaceContext(text, replaceMap);
                log.info("接收者邮箱：{}", accepterEmail.get(i));

                UndeliveredEmail emailInfoData = initSendEmailData.sendEmail(subject, text, session, accepterEmail.get(i));
                if (emailInfoData.getError_code().equals(200)){
                    deliveryAmount++;
                }else {
                    bounce_amount++;
                }
                emailInfoOut(emailInfoData, accepterEmail.get(i), emailModel.getAccepterName().get(i));
                email.setEnd_date(System.currentTimeMillis() / 1000);
                emailTaskRepository.save(email);
                emailReportRepository.addDeliveryAmount(emailModel.getOperationId(), deliveryAmount); //送达数量更新
                emailReportRepository.addBounceAmountById(emailModel.getOperationId(), bounce_amount); //退信数量更新
            }
            EmailStatue emailStatue = emailStatueRepository.findByTaskId(emailModel.getOperationId());
            emailStatue.setEmail_status(6); //标记已完成
            emailStatueRepository.save(emailStatue);


        }else if(email.getTask_type() == 2){ //循环发送
            text = emailContent;
            if (email.getIndex() < 0 ||email.getIndex() - 1 >= accepterEmail.size()){
                log.warn("循环邮件下标越界  index:{} 接受者一共有 {}", email.getIndex(), accepterEmail.size());
                email.setEnd_date(System.currentTimeMillis() / 1000);
                emailTaskRepository.save(email);
                EmailStatue emailStatue = emailStatueRepository.findByTaskId(emailModel.getOperationId());
                emailStatue.setEmail_status(6); //标记已完成
                emailStatueRepository.save(emailStatue);
                return;
            }

            if ((emailCustomer = emailCustomerRepository.findByEmail(accepterEmail.get(email.getIndex() - 1))) != null){
                replacePrivateContext(replaceMap, emailCustomer.getCustomer_name(), emailCustomer.getSex(),emailCustomer.getBirth(), emailCustomer.getCustomer_country_id(), email.getEmail_task_id(), accepterEmail.get(email.getIndex() - 1), emailCustomer.getContact_person(), emailCustomer.getContact_way());

            }else if((emailSupplier = emailSupplierRepository.findByEmail(accepterEmail.get(email.getIndex() - 1))) != null){
                replacePrivateContext(replaceMap, emailSupplier.getSupplier_name(), emailSupplier.getSex(), emailSupplier.getBirth(), emailSupplier.getSupplier_country_id(),email.getEmail_task_id(), accepterEmail.get(email.getIndex() - 1), emailSupplier.getContact_person(), emailSupplier.getContact_way());

            }
            text = replaceContext(text, replaceMap);
            System.out.println("邮箱内容：" +  text);
            log.info("循环发送接收者邮箱：{}", accepterEmail.get(email.getIndex() - 1));
            UndeliveredEmail emailInfoData = initSendEmailData.sendEmail(subject, text, session, accepterEmail.get(email.getIndex() - 1));
            emailInfoOut(emailInfoData, accepterEmail.get(email.getIndex() - 1), emailModel.getAccepterName().get(email.getIndex() - 1));
            if (emailInfoData.getError_code().equals(200)){
                deliveryAmount++;
            }else {
                bounce_amount++;
            }

            emailReportRepository.addDeliveryAmount(emailModel.getOperationId(), deliveryAmount); //送达数量更新
            emailReportRepository.addBounceAmountById(emailModel.getOperationId(), bounce_amount); //退信数量更新

            if (email.getIndex() == accepterEmail.size()){
                email.setEnd_date(System.currentTimeMillis() / 1000);
                emailTaskRepository.save(email);
                EmailStatue emailStatue = emailStatueRepository.findByTaskId(emailModel.getOperationId());
                emailStatue.setEmail_status(6); //标记已完成
                emailStatueRepository.save(emailStatue);
            }
        }

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
        emailInfoData.setStart_date(Long.valueOf(emailModel.getSendTime()));
        emailInfoData.setEnd_date(nowTime);
        emailInfoData.setOpened(1L);
        emailDataInfo.save(emailInfoData);

        if (emailInfoData.getError_code().equals(535) || emailInfoData.getError_code().equals(500)){
            //插入到未送达的重发表中去
            EmailFail emailFail = new EmailFail();
            emailFail.setEmail_resend_id(uuid);
            emailFail.setAccepter_email(acceptEmail);
            emailFail.setStatus(0L);
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
        String emailContent = emailContentRepository.findContentById(emailModel.getOperationId());

        if (emailFail == null){
            log.error("没有找到重发邮件");
            return;
        }
        EmailTask email = emailTaskRepository.findByTaskId(this.emailModel.getOperationId());
        //替换文本
        HashMap<String, String> replaceMap = new HashMap<>();

        //replacePublicContext(email, replaceMap);
        String accepterEmail = emailFail.getAccepter_email();

        InitSendEmailData initSendEmailData = new InitSendEmailData(emailModel.getSenderEmail(), emailModel.getHost(), emailModel.getAuthCode());
        Session session = initSendEmailData.createSSLSocket();

        EmailCustomer emailCustomer;
        EmailSupplier emailSupplier;

        if ((emailCustomer = emailCustomerRepository.findByEmail(accepterEmail)) != null){
            System.out.println("收件人信息替换curstomer" + emailCustomer.toString());
            replacePrivateContext(replaceMap, emailCustomer.getCustomer_name(), emailCustomer.getSex(),emailCustomer.getBirth(), emailCustomer.getCustomer_country_id(), email.getEmail_task_id(), accepterEmail, emailCustomer.getContact_person(), emailCustomer.getContact_way());
        }else if((emailSupplier = emailSupplierRepository.findByEmail(accepterEmail)) != null){
            System.out.println("收件人信息替换supplier" + emailSupplier.toString());
            replacePrivateContext(replaceMap, emailSupplier.getSupplier_name(), emailSupplier.getSex(), emailSupplier.getBirth(), emailSupplier.getSupplier_country_id(),email.getEmail_task_id(), accepterEmail, emailSupplier.getContact_person(), emailSupplier.getContact_way());
        }

        String text = replaceContext(emailContent, replaceMap);
        System.out.println("邮箱内容：" +  text);

        UndeliveredEmail undeliveredEmail = initSendEmailData.sendEmail(email.getSubject(), text, session, accepterEmail);

        if (undeliveredEmail.getError_code().equals(200)){
            //代表重发成功
            //写入emailfail
            emailFail.setStatus(1L);
            emailFail.setEnd_time(System.currentTimeMillis() / 1000);
            emailFail.setError_msg("邮件重发成功");
            emailFail.setStart_time(Long.valueOf(emailModel.getSendTime()));
            log.info("email_id :{}重发成功", emailModel.getOperationId());
            emailFailRepository.save(emailFail);
        }else{
            //代表重发失败
            //写入emailfail
            emailFail.setStatus(2L);
            emailFail.setEnd_time(System.currentTimeMillis() / 1000);
            emailFail.setStart_time(Long.valueOf(emailModel.getSendTime()));
            emailFail.setError_msg(undeliveredEmail.getError_msg());
            emailFailRepository.save(emailFail);
            log.info("email_id :{}重发失败", emailModel.getOperationId());
        }

    }

    private void replacePublicContext(EmailTask emailTask, HashMap<String, String> hashMap){
        hashMap.put("联系人", emailTask.getSender_name());
        hashMap.put("联系方式", emailModel.getSenderEmail());
    }

    private void replacePrivateContext(HashMap<String, String> hashMap,String name, String sex, String date, String country, String email_task, String userEmail, String person, String personPhone){
        hashMap.put("联系人", person);
        hashMap.put("联系方式", personPhone);

        hashMap.put("收件人姓名",name);
        country = emailCountryRepository.mapCountry(country);
        hashMap.put("收件人国家", country);
        hashMap.put("性别", sex);
        hashMap.put("出生日期", date);
        hashMap.put("emailTaskId", email_task);
        hashMap.put("receiverEmail", userEmail);
    }


    public String replaceContext(String text, HashMap<String,String> replaceMap){
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            if (entry.getValue() != null) {
                System.out.println("替换key:" + entry.getKey() + "替换value" + entry.getValue());
                text = text.replace("${" + entry.getKey() + "}", entry.getValue());
            }
        }
        System.out.println("替换后的文本：" + text);
        return text;
    }

}
