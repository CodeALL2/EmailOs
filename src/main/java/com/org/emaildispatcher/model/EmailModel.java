package com.org.emaildispatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 邮件在rocket里面的对象实体
 */
public class EmailModel implements Serializable {
    private String resendId; //重发邮件的id，为空不管
    private String operationId; //邮件的业务id
    private String redisKey; //邮件内容在redis中的key
    private String host; //邮件服务器类型  smtp.qq.com, smtp.163.com ...
    private String senderEmail; //发件人邮箱
    private String senderName; //发件人Name
    private String phone; //发件人联系方式
    private String authCode; //授权码
    private ArrayList<String> accepterEmail; //接受者的邮箱
    private ArrayList<String> accepterName; //接受者的邮箱Name
    private String sendTime;    //邮件发送时间，也就是投递到rocketmq队列里面的时间点
    private String acceptTime;    //邮件发送成功、失败时间点
}

