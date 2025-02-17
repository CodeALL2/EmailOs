package com.org.emaildispatcher.timer;

import com.org.emaildispatcher.brige.BufferQueue;
import com.org.emaildispatcher.emailstatuefunc.EmailStatueFunction;
import com.org.emaildispatcher.handler.EmailSendTypeHandler;
import com.org.emaildispatcher.mapper.EmailStatueRepository;
import com.org.emaildispatcher.mapper.EmailTaskRepository;
import com.org.emaildispatcher.model.BufferData;
import com.org.emaildispatcher.model.EmailStatue;
import com.org.emaildispatcher.model.EmailTask;
import com.org.emaildispatcher.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

@Component
@Slf4j
public class TimeTask {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private EmailSendTypeHandler emailSendTypeHandler;

    @Resource
    private EmailStatueFunction emailStatueFunction;

    @Resource
    private EmailTaskRepository emailTaskRepository;
    @Resource
    private EmailStatueRepository emailStatueRepository;

    private static HashMap<Integer, Method> emailStatueHashMap = new HashMap<Integer, Method>();

    static {
        try {
            emailStatueHashMap.put(2, EmailStatueFunction.class.getMethod("statueTwoFunc", EmailStatue.class));
            emailStatueHashMap.put(3, EmailStatueFunction.class.getMethod("statueThreeFunc", EmailStatue.class));
            emailStatueHashMap.put(4, EmailStatueFunction.class.getMethod("statueFourFunc", EmailStatue.class));
        } catch (NoSuchMethodException e) {
            log.error("邮件状态码执行方法没有发现{}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void executeTasks() {
        log.info("定时任务被执行");
        long nowTime = System.currentTimeMillis() / 1000;

        // 获取所有过期的任务
        Set<Object> tasksToExecute = redisUtil.getTimerTask(nowTime);
        if (tasksToExecute == null || tasksToExecute.isEmpty()) {
            log.info("当前没有需要执行的定时任务");
            return;
        }

        for (Object value : tasksToExecute) {
            String emailId = (String) value;
            //重发的业务key  resend|3
            String[] parts = emailId.split("\\|"); // 按竖线拆分
            if (parts[0].equals("resend")){
                //说明是重发的逻辑 直接将id 丢给发送队列
                log.info("重发包 key:{}", emailId);
                redisUtil.deleteZSetKey(emailId);
                BufferQueue.putData(new BufferData(emailId));
                continue;
            }

            EmailTask emailModel = null;
            try {
                //从elasticsearch中读取邮件实体
                emailModel = emailTaskRepository.findByTaskId(emailId);
                if (emailModel == null) {
                    log.warn("任务{}不存在或已被清除", emailId);
                    redisUtil.deleteZSetKey(emailId); // 删除无效任务的 key
                    //将任务状态置为异常
                    EmailStatue s = emailStatueRepository.findByTaskId(emailId);
                    if (s != null) {
                        s.setEmail_status(5);
                        emailStatueRepository.save(s);
                    }
                    continue;
                }

                log.info("取出任务{}，任务详情：{}", emailId, emailModel);
                //替换成从elasticsearch 状态表中读取状态
                EmailStatue emailStatue = emailStatueRepository.findByTaskId(emailId);

                if (emailStatue == null) {
                    log.warn("任务 {} 的状态码不存在，跳过处理", emailId);
                    continue;
                }

                //邮件状态不为空  邮件码不为1 1开始 2暂停 3终止 4重置
                if (!emailStatue.getEmail_status().equals(1)){
                    Method statueMethod = emailStatueHashMap.get(emailStatue.getEmail_status());
                    if (statueMethod != null){
                        statueMethod.invoke(emailStatueFunction, emailStatue);
                    }else {
                        //将任务状态置为异常
                        EmailStatue s = emailStatueRepository.findByTaskId(emailId);
                        if (s != null) {
                            s.setEmail_status(5);
                            emailStatueRepository.save(s);
                        }
                        log.error("未找到状态码{}对应的执行方法", emailStatue);
                        redisUtil.deleteZSetKey(emailId);
                    }
                    continue; //如果状态码为2 或者 3 说明被暂停或者被终止
                }

                switch (emailModel.getTask_type()) {
                    case 0 -> emailSendTypeHandler.handleSingleEmail(emailModel.getEmail_task_id()); // 单封邮件
                    case 2 -> emailSendTypeHandler.handleScheduledEmail(emailModel.getEmail_task_id()); // 定时邮件
                    case 1 -> emailSendTypeHandler.handleLoopEmail(emailModel.getEmail_task_id(), emailModel, nowTime); // 循环邮件
                    default -> redisUtil.deleteZSetKey(emailId);
                }
                // 将当前任务加入缓冲队列
                log.info("待发送信息:{}   {}", emailId, emailModel);
                BufferQueue.putData(new BufferData(emailId));
            } catch (Exception e) {
                log.error("处理任务{}时发生异常，任务将被移除。异常信息：{}", emailId, e.getMessage());
                redisUtil.deleteZSetKey(emailId);
                EmailStatue emailStatueModel = emailStatueRepository.findByTaskId(emailId);
                emailStatueModel.setEmail_status(5);
                emailStatueRepository.save(emailStatueModel);
            }
        }
    }
}
