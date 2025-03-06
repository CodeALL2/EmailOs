package com.org.sendmail.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "email_task") // 指定索引名称
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailTask {
    private String email_id; //邮件id

    private String email_task_id; //邮件任务id

    private String email_type_id; //邮件类型id

    private Integer task_type;  //任务类型

    private Integer task_cycle; //任务循环周期

    //发件人id
    private String sender_id;

    //发件人name
    private String sender_name;

    private ArrayList<String> shadow_id; //影子用户id

    private ArrayList<String> receiver_id; //收件人id


    private ArrayList<String> receiver_name; //收件人name

    //private ArrayList<String> attachment; //附件的url

    private String template_id; //模板id


    private String subject; //邮件主题


    private String email_content; //邮件内容


    private Integer bounce_amount; //退信数量

    private Integer unsubscribe_amount; //退订数量


    private Long created_at; //任务创建时间


    private Long start_date; //邮件任务开始时间


    private Long end_date; //邮件任务的结束时间


    private Integer interval_date; //任务的间隔时间，秒级时间戳


    private Integer index; //循环任务的发送下标;
}
