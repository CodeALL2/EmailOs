package com.org.emaildispatcher.handler;

import com.org.emaildispatcher.mapper.EmailTaskRepository;
import com.org.emaildispatcher.model.EmailTask;
import com.org.emaildispatcher.util.RedisUtil;
import com.org.emaildispatcher.util.TimestampToDate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailSendTypeHandler {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private EmailTaskRepository emailTaskRepository;

    /**
     * 处理单封邮件任务
     */
    public void handleSingleEmail(String emailId) {
        log.info("任务{}为单封邮件，直接发送", emailId);
        redisUtil.deleteZSetKey(emailId); // 删除顺序队列中的消息 key
    }

    /**
     * 处理定时邮件任务
     */
    public void handleScheduledEmail(String emailId) {
        log.info("任务{}为定时邮件，直接发送", emailId);
        redisUtil.deleteZSetKey(emailId); // 删除顺序队列中的消息 key
    }

    /**
     * 处理循环邮件任务
     */
    public void handleLoopEmail(String emailId, EmailTask emailModel, long nowTime) {
        if (emailModel.getIndex() == emailModel.getReceiver_id().size() - 1) {
            log.info("任务{}为循环邮件的最后一封，直接发送", emailId);
            emailModel.setIndex(emailModel.getIndex() + 1); // 指针移动
            emailTaskRepository.save(emailModel);
            redisUtil.deleteZSetKey(emailId); // 删除顺序队列中的消息 key
        } else {
            log.info("任务{}为循环邮件，准备发送下一封", emailId);
            long nextEmailTime = Long.parseLong(String.valueOf(emailModel.getInterval_date())) + nowTime;
            emailModel.setIndex(emailModel.getIndex() + 1); // 指针移动
            emailModel.setStart_date(nextEmailTime); // 设置下一封发送时间
            log.info("任务{}的下一封邮件发送时间为：{}", emailId, TimestampToDate.toTime(nextEmailTime));
            emailTaskRepository.save(emailModel);
            redisUtil.deleteZSetKey(emailId); // 删除顺序队列中的消息 key
            redisUtil.addTimerTask(String.valueOf(emailModel.getEmail_task_id()), nextEmailTime); // 重新加入定时任务
        }
    }
}
