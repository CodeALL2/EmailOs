package com.org.sendmail.common;

import com.org.sendmail.model.EmailModel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 邮件缓冲队列 防止线程池被高吞吐量压垮
 *
 * <p>该类处理邮件的接收、分类，并将邮件交给相应的队列进行处理。</p>
 *
 * @author cbs
 * @version 1.0
 * @since 2024-11-26
 */
@Slf4j
public class BufferQueue {

    private static ArrayBlockingQueue<EmailModel> emailQueue = new ArrayBlockingQueue<>(1000);  // 容量为 1000 的队列
    private static final Logger logger = LoggerFactory.getLogger(BufferQueue.class);

    public static void sendEmailToQueue(EmailModel emailModel) {
        try {
            logger.info("邮件信息: {}", emailModel);
            emailQueue.put(emailModel);
            logger.info("邮件已成功投递到emailQueue");
        } catch (InterruptedException e) {
            logger.error("邮件投递emailQueue失败 错误信息:{}", e);
            e.printStackTrace();
        }
    }

    public static EmailModel getEmailFromQueue(){
        EmailModel email = null;
        try {
            email = emailQueue.take();
            logger.info("邮件已从emailQueue中取出: {}", email);
        } catch (InterruptedException e) {
            logger.error("邮件取出emailQueue失败 错误信息:{}", e);
        }
        return email;
    }

}
