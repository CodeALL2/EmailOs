package com.org.sendmail.birth;


import com.org.sendmail.Util.InitSendEmailData;
import com.org.sendmail.mapper.*;
import com.org.sendmail.model.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.Session;
import java.util.*;

@Slf4j
@Component
public class BirthTimeTask {

    @Resource
    private EmailStatueRepository emailStatueRepository;

    @Resource
    private EmailCustomerRepository emailCustomerRepository;

    @Resource
    private EmailSupplierRepository emailSupplierRepository;

    @Resource
    private EmailUserRepository emailUserRepository;

    @Resource
    private EmailTaskRepository emailTaskRepository;

    @Resource
    private EmailDataInfo emailDataInfo;

    @Resource
    private EmailFailRepository emailFailRepository;

    @Resource
    private EmailCountryRepository emailCountryRepository;

    @Resource
    private EmailContentRepository emailContentRepository;

    @Resource
    private EmailReportRepository emailReportRepository;

    private int totalSum; //总共

    private int bounceAmount;  //退信

    private int deliveryAmount; //送达


    //@Scheduled(cron = "0 0 0 * * ?") 凌晨执行
    @Scheduled(cron = "0 * * * * ?") //分钟触发
    public void executeTasks(){
        //当前时间的时间戳
        long nowTime = System.currentTimeMillis() / 1000;
        nowTime = nowTime - 7200;
        log.info("当前时间戳:{}", nowTime);

        log.info("生日定时器被触发");
        //查询生日状态状态 birth|0关闭 birth|1开启
        EmailStatue birthStatue = emailStatueRepository.findByTaskId("birth");

        if (birthStatue == null || birthStatue.getEmail_status().equals(2)){
            //说明状态未开启
            log.info("生日功能未开启");
            return ;
        }

        //邮件任务载体
        EmailTask emailTask = emailTaskRepository.findByTaskId("birth");

        //查询当天生日的用户，并分配给所有大管理发送
        List<EmailCustomer> allCustomer = emailCustomerRepository.findAllCustomer();
        List<EmailSupplier> allSupplier = emailSupplierRepository.findAll();
        List<EmailUser> allBoos = emailUserRepository.findAllBoos(2);

        if (allCustomer == null || allSupplier == null){
            log.warn("当天没有用户生日");
            return;
        }
        //算出总人数
        int sumPerson = allCustomer.size() + allSupplier.size();
        int managerCount = allBoos.size();

        log.info("当天有{}个用户生日", sumPerson);

        this.totalSum = sumPerson;

        if (managerCount == 0 || sumPerson == 0) {
            log.warn("没有管理者或没有用户在当天生日");
            return;
        }

// 创建一个映射，每个管理员对应一个任务列表
        Map<EmailUser, List<Object>> taskDistribution = new HashMap<>();
        for (EmailUser manager : allBoos) {
            taskDistribution.put(manager, new ArrayList<>());
        }

// 轮询分配任务
        int index = 0;
        List<Object> allUsers = new ArrayList<>();
        allUsers.addAll(allCustomer);
        allUsers.addAll(allSupplier);

        for (Object user : allUsers) {
            EmailUser manager = allBoos.get(index % managerCount); // 轮询分配
            taskDistribution.get(manager).add(user);
            index++;
        }

// 输出分配结果
        for (Map.Entry<EmailUser, List<Object>> entry : taskDistribution.entrySet()) {
            log.info("管理员: {} 分配了 {} 个任务", entry.getKey().getUser_name(), entry.getValue().size());
        }

        String emailContent = emailContentRepository.findContentById("birth");
        //替换文本
        HashMap<String, String> replaceMap = new HashMap<>();

        for (EmailUser user: allBoos){

            List<Object> objects = taskDistribution.get(user);
            if (objects == null || objects.size() == 0 ){
                continue;
            }

            InitSendEmailData initSendEmailData = new InitSendEmailData(user.getUser_email(), user.getUser_host(), user.getUser_email_code());
            Session session = initSendEmailData.createSSLSocket();

            for (Object o : objects){
                String text = emailContent;
                if (o instanceof EmailCustomer){
                    //构建邮件发送出去
                    String userEmail = ((EmailCustomer) o).getEmails().get(0);

                    replacePrivateContext(replaceMap, ((EmailCustomer) o).getCustomer_name(), ((EmailCustomer) o).getSex(),((EmailCustomer) o).getBirth(), ((EmailCustomer) o).getCustomer_country_id(), ((EmailCustomer) o).getContact_person(), ((EmailCustomer) o).getContact_way());
                    String replaceContext = replaceContext(text, replaceMap);
                    send(emailTask, replaceContext, session, userEmail, ((EmailCustomer) o).getCustomer_name(), initSendEmailData, user.getUser_email(), user.getUser_name());
                }else if (o instanceof EmailSupplier){
                    String userEmail = ((EmailSupplier) o).getEmails().get(0);
                    replacePrivateContext(replaceMap, ((EmailSupplier) o).getSupplier_name(), ((EmailSupplier) o).getSex(),((EmailSupplier) o).getBirth(), ((EmailSupplier) o).getSupplier_country_id(),((EmailSupplier) o).getContact_person(),((EmailSupplier) o).getContact_way());
                    String replaceContext = replaceContext(text, replaceMap);
                    send(emailTask, replaceContext, session, userEmail, ((EmailSupplier) o).getSupplier_name(), initSendEmailData, user.getUser_email(), user.getUser_name());
                }
            }
        }
        emailReportRepository.addBounceAmountById("birth", bounceAmount);
        log.info("生日发送退信数:{}", bounceAmount);
        emailReportRepository.addDeliveryAmount("birth", deliveryAmount);
        log.info("生日发送成功数:{}", deliveryAmount);
        emailReportRepository.updateEmailTotal("birth", totalSum);
        log.info("当天生日一共有{}", totalSum);


    }

    public void send(EmailTask emailTask, String text, Session session, String accepterEmail, String acceptName, InitSendEmailData initSendEmailData, String senderEmail, String senderName){
        UndeliveredEmail emailInfoData = initSendEmailData.sendEmail(emailTask.getSubject(), text, session, accepterEmail);
        if (emailInfoData.getError_code().equals(500) || emailInfoData.getError_code().equals(535)){
            this.bounceAmount++;
        }else {
            this.deliveryAmount++;
        }

        emailInfoOut(emailTask,emailInfoData, accepterEmail, acceptName, senderEmail, senderName);
    }

    public void emailInfoOut(EmailTask emailTask,UndeliveredEmail emailInfoData, String acceptEmail, String acceptEmailName, String senderEmail, String senderName){
        Long nowTime = System.currentTimeMillis() / 1000;
        emailInfoData.setEmail_task_id("birth");
        // 生成一个随机的UUID
        String uuid = UUID.randomUUID().toString();
        emailInfoData.setEmail_id(uuid);
        emailInfoData.setSender_id(senderEmail);
        emailInfoData.setReceiver_id(acceptEmail);
        emailInfoData.setSender_name(senderName);
        emailInfoData.setReceiver_name(acceptEmailName);
        emailInfoData.setStart_date(nowTime);
        emailInfoData.setEnd_date(nowTime);
        emailInfoData.setOpened(1L);
        emailInfoData.setSubject(emailTask.getSubject());
        emailDataInfo.save(emailInfoData);

        if (emailInfoData.getError_code().equals(535) || emailInfoData.getError_code().equals(500)){
            //插入到未送达的重发表中去
            EmailFail emailFail = new EmailFail();
            emailFail.setEmail_resend_id(uuid);
            emailFail.setAccepter_email(acceptEmail);
            emailFail.setStatus(0L);
            emailFail.setEmail_task_id("birth");
            emailFailRepository.save(emailFail);
        }
    }

    private void replacePublicContext(String sendEmailName, String email,HashMap<String, String> hashMap){
        hashMap.put("联系人", sendEmailName);
        hashMap.put("联系方式", email);
    }

    private void replacePrivateContext(HashMap<String, String> hashMap,String name, String sex, String date, String country, String person, String personPhone){
        hashMap.put("联系人", person);
        hashMap.put("联系方式", personPhone);
        country = emailCountryRepository.mapCountry(country);
        hashMap.put("收件人姓名",name);
        hashMap.put("收件人国家", country);
        hashMap.put("性别", sex);
        hashMap.put("出生日期", date);
    }

    public String replaceContext(String text, HashMap<String,String> replaceMap){
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            System.out.println("替换key:" + entry.getKey() + "替换value" + entry.getValue());
            if (entry.getValue() != null) {
                System.out.println("替换key:" + entry.getKey() + "替换value" + entry.getValue());
                text = text.replace("${" + entry.getKey() + "}", entry.getValue());
            }
        }

        return text;
    }
}
