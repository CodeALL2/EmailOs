package com.org.sendmail.model;

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
    private Integer operationId; //邮件的业务id
    private String redisKey; //邮件内容在redis中的key
    private String host; //邮件服务器类型  smtp.qq.com, smtp.163.com ...
    private String senderEmail; //发件人邮箱
    private String authCode; //授权码
    private ArrayList<String> accepterEmail; //接受者的邮箱
}

