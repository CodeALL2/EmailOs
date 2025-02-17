package com.org.sendmail.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "email_task") // 指定索引名称
public class EmailTask {
    @Id
    private String email_id; //邮件id

    @Field(type = FieldType.Keyword)
    private String email_task_id; //邮件任务id

    @Field(type = FieldType.Keyword)
    private String email_type_id; //邮件类型id

    @Field(type = FieldType.Integer)
    private Integer task_type;  //任务类型

    @Field(type = FieldType.Integer)
    private Integer task_cycle; //任务循环周期

    @Field(type = FieldType.Keyword)//发件人id
    private String sender_id;

    @Field(type = FieldType.Keyword)//发件人name
    private String sender_name;

    @Field(type = FieldType.Keyword)
    private ArrayList<String> shadow_id; //影子用户id

    @Field(type = FieldType.Keyword)
    private ArrayList<String> receiver_id; //收件人id

    @Field(type = FieldType.Keyword)
    private ArrayList<String> receiver_name; //收件人name

    @Field(type = FieldType.Keyword)
    private String attachment; //附件的url

    @Field(type = FieldType.Keyword)
    private String template_id; //模板id

    @Field(type = FieldType.Keyword)
    private String subject; //邮件主题

    @Field(type = FieldType.Keyword)
    private String email_content; //邮件内容

    @Field(type = FieldType.Integer)
    private Integer bounce_amount; //退信数量

    @Field(type = FieldType.Integer)
    private Integer unsubscribe_amount; //退订数量

    @Field(type = FieldType.Long)
    private Long created_at; //任务创建时间

    @Field(type = FieldType.Long)
    private Long start_date; //邮件任务开始时间

    @Field(type = FieldType.Long)
    private Long end_date; //邮件任务的结束时间

    @Field(type = FieldType.Integer)
    private Integer interval_date; //任务的间隔时间，秒级时间戳

    @Field(type = FieldType.Integer)
    private Integer index; //循环任务的发送下标;
}
