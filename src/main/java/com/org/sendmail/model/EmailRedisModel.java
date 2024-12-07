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
 * 邮件在redis里面的实体
 */
public class EmailRedisModel implements Serializable {
    //发送者邮箱 例如:12345678@qq.com
    private String senderEmail;
    //僵尸用户的邮件id
    private ArrayList<String> shadowList;
    //接受者邮箱
    private ArrayList<String> accepterEmailList;
    //邮件主题
    private String subject;
    //邮件内容
    private String text;
    //业务id号
    private Integer operationId;
    //目标用户希望发送的时间点
    private String time;
    //邮件类型
    private int type;
    //频率设置
}
