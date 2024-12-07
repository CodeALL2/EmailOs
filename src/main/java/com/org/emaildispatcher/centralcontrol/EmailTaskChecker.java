package com.org.emaildispatcher.centralcontrol;

import com.org.emaildispatcher.model.EmailRedisModel;


/**
 * 检查邮件是否完整
 */
public class EmailTaskChecker {

    public static boolean checkEmail(EmailRedisModel emailRedisModel){
        return  emailRedisModel !=null &&
                emailRedisModel.getSenderEmail() != null &&
                emailRedisModel.getAccepterEmailList() != null &&
                emailRedisModel.getText() != null &&
                emailRedisModel.getSubject() != null;
    }
}
