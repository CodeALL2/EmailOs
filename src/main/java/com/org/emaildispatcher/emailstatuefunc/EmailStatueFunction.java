package com.org.emaildispatcher.emailstatuefunc;

import com.org.emaildispatcher.mapper.EmailPausedRepository;
import com.org.emaildispatcher.mapper.EmailStatueRepository;
import com.org.emaildispatcher.mapper.EmailTaskRepository;
import com.org.emaildispatcher.model.EmailPaused;
import com.org.emaildispatcher.model.EmailStatue;
import com.org.emaildispatcher.model.EmailTask;
import com.org.emaildispatcher.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailStatueFunction {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private EmailPausedRepository emailPausedRepository;
    @Resource
    private EmailTaskRepository emailTaskRepository;
    @Resource
    private EmailStatueRepository emailStatueRepository;

    //1开始 2暂停 3终止 4重置
    /**
     * 邮件状态码为2
     */
    public void statueTwoFunc(EmailStatue emailStatue){
        log.info("状态码 2 暂停方法被执行");
        //替换成放入elasticsearch中的暂停表
        EmailPaused emailPaused = new EmailPaused();
        emailPaused.setId(emailStatue.getEmail_task_id());
        emailPaused.setEmail_task_id(emailStatue.getEmail_task_id());
        emailPausedRepository.save(emailPaused);
        //删除顺序队列中的消息 key
        redisUtil.deleteZSetKey(String.valueOf(emailStatue.getEmail_task_id()));
    }

    /**
     * 邮件状态码为3
     */
    public void statueThreeFunc(EmailStatue emailStatue){
        log.info("状态码 3 终止方法被执行");
        redisUtil.deleteZSetKey(String.valueOf(emailStatue.getEmail_task_id()));
    }

    /**
     * 邮件状态码为4
     */
    public void statueFourFunc(EmailStatue emailStatue){
        //获取邮件实体
        EmailTask emailModel = emailTaskRepository.findByTaskId(emailStatue.getEmail_task_id());
        log.info("状态码 4 重置方法被执行");
        if (emailModel != null) {
//            log.info("{} 邮件发送时间已重置", emailStatue.getEmail_task_id());
//            long startTime = emailModel.getStart_date();
//            long endTime = emailModel.getEnd_date();
//            long nowTime = System.currentTimeMillis() / 1000;
//            //重置截止时间 计算公式为 截止时间加上(当前时间减去 startTime)
//            emailModel.setEnd_date(endTime + nowTime - startTime);
//            emailModel.setStart_date(nowTime);
//            emailModel.setIndex(0);
            //替换成将elasticsearch中的邮件属性重置
            //log.info("{} 邮件已在redis中重置", emailStatue.getEmail_task_id());
            //将邮件重新塞入到es中
            //emailTaskRepository.save(emailModel);
            //将elasticsearch中的邮件状态表改为1
            //log.info("{} 邮件已在邮件状态表中重置", emailModel.getEmail_task_id());
            //EmailStatue emailStatueModel = emailStatueRepository.findByTaskId(emailStatue.getEmail_task_id());
            //emailStatueModel.setEmail_status(6);
            //log.info("{} 邮件状态已修改为完成", emailStatue.getEmail_task_id());
            //emailStatueRepository.save(emailStatueModel);
            log.info("{} 邮件已在邮件发送队列中移出", emailStatue.getEmail_task_id());
            redisUtil.deleteZSetKey(emailStatue.getEmail_task_id());
//            log.info("{} 邮件已添加到发送队列", emailStatue.getEmail_task_id());
//            redisUtil.addTimerTask(emailStatue.getEmail_task_id(), nowTime);
        }
    }
}
