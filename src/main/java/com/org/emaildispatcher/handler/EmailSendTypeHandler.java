package com.org.emaildispatcher.handler;

import com.org.emaildispatcher.mapper.EmailDataInfo;
import com.org.emaildispatcher.mapper.EmailFailRepository;
import com.org.emaildispatcher.mapper.EmailTaskRepository;
import com.org.emaildispatcher.model.EmailFail;
import com.org.emaildispatcher.model.EmailTask;
import com.org.emaildispatcher.model.UndeliveredEmail;
import com.org.emaildispatcher.util.RedisUtil;
import com.org.emaildispatcher.util.TimestampToDate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class EmailSendTypeHandler {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private EmailTaskRepository emailTaskRepository;

    @Resource
    private EmailDataInfo emailDataInfo;

    @Resource
    private EmailFailRepository emailFailRepository;

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
            log.info("任务{}为循环邮件的最后一封", emailId);
            if (emailModel.getEnd_date() < nowTime) {
                UndeliveredEmail emailInfoData = new UndeliveredEmail();
                emailInfoData.setError_code(500);
                emailInfoData.setError_msg("循环时间不足");
                emailInfoData.setEmail_task_id(emailModel.getEmail_task_id());
                // 生成一个随机的UUID
                String uuid = UUID.randomUUID().toString();
                emailInfoData.setEmail_id(uuid);
                emailInfoData.setSender_id(emailModel.getSender_id());
                emailInfoData.setReceiver_id(emailModel.getReceiver_id().get(emailModel.getIndex() - 1));
                emailInfoData.setSender_name(emailModel.getSender_name());
                emailInfoData.setReceiver_name(emailModel.getReceiver_name().get(emailModel.getIndex() - 1));
                emailInfoData.setStart_date(nowTime);
                emailInfoData.setEnd_date(nowTime);
                emailInfoData.setOpened(1L);
                emailDataInfo.save(emailInfoData);

                EmailFail emailFail = new EmailFail();
                emailFail.setEmail_resend_id(uuid);
                emailFail.setAccepter_email(emailModel.getReceiver_id().get(emailModel.getIndex() - 1));
                emailFail.setStatus(0L);
                emailFail.setEmail_task_id(emailModel.getEmail_task_id());
                emailFailRepository.save(emailFail);
                return;
            }
            emailModel.setIndex(emailModel.getIndex() + 1); // 指针移动
            emailTaskRepository.save(emailModel);
            redisUtil.deleteZSetKey(emailId); // 删除顺序队列中的消息 key
        } else {
            log.info("任务{}为循环邮件，准备发送下一封", emailId);

            //如果结束时间戳小于当前时间戳 所有邮件记录到失败表和详情表中去
            if (emailModel.getEnd_date() < nowTime){
                for (int i = emailModel.getIndex(); i < emailModel.getReceiver_id().size(); i++){
                    UndeliveredEmail emailInfoData = new UndeliveredEmail();
                    emailInfoData.setError_code(500);
                    emailInfoData.setError_msg("循环时间不足");
                    emailInfoData.setEmail_task_id(emailModel.getEmail_task_id());
                    // 生成一个随机的UUID
                    String uuid = UUID.randomUUID().toString();
                    emailInfoData.setEmail_id(uuid);
                    emailInfoData.setSender_id(emailModel.getSender_id());
                    emailInfoData.setReceiver_id(emailModel.getReceiver_id().get(emailModel.getIndex() - 1));
                    emailInfoData.setSender_name(emailModel.getSender_name());
                    emailInfoData.setReceiver_name(emailModel.getReceiver_name().get(emailModel.getIndex() - 1));
                    emailInfoData.setStart_date(nowTime);
                    emailInfoData.setEnd_date(nowTime);
                    emailInfoData.setOpened(1L);
                    emailDataInfo.save(emailInfoData);

                    EmailFail emailFail = new EmailFail();
                    emailFail.setEmail_resend_id(uuid);
                    emailFail.setAccepter_email(emailModel.getReceiver_id().get(emailModel.getIndex() - 1));
                    emailFail.setStatus(0L);
                    emailFail.setEmail_task_id(emailModel.getEmail_task_id());
                    emailFailRepository.save(emailFail);
                }
                return;
            }
            //计算间隔计算公式 = 结束时间戳减去当前时间戳 / 当前总人数减去正在发送的下标
            long interval = (emailModel.getEnd_date() - nowTime) / emailModel.getReceiver_id().size() - emailModel.getIndex();

            emailModel.setIndex(emailModel.getIndex() + 1); // 指针移动
            //emailModel.setStart_date(nextEmailTime); // 设置下一封发送时间
            log.info("任务{}的下一封邮件发送时间为：{}", emailId, nowTime + interval);
            emailTaskRepository.save(emailModel);
            redisUtil.deleteZSetKey(emailId); // 删除顺序队列中的消息 key
            redisUtil.addTimerTask(String.valueOf(emailModel.getEmail_task_id()), nowTime + interval); // 重新加入定时任务
        }
    }
}
