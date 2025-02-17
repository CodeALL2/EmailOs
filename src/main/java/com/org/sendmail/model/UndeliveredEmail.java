package com.org.sendmail.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "undelivered_email")
public class UndeliveredEmail {
    @Id
    private String email_id; //主键

    @Field(type = FieldType.Keyword)
    private String email_task_id; //邮件任务id

    @Field(type = FieldType.Keyword)
    private String sender_id; //发件人id

    @Field(type = FieldType.Keyword)
    private String sender_name; //发件人name

    @Field(type = FieldType.Keyword)
    private String receiver_id; //收件人id

    @Field(type = FieldType.Keyword)
    private String receiver_name; //收件人name

    @Field(type = FieldType.Integer)
    private Integer error_code; //错误状态码

    @Field(type = FieldType.Keyword)
    private String error_msg; //错误信息

    @Field(type = FieldType.Keyword)
    private Long start_date; //邮件任务的启动时间

    @Field(type = FieldType.Keyword)
    private Long end_date; //邮件的投递


    @Field(type = FieldType.Long)
    private Long created_at; //任务创建时间

    @Field(type = FieldType.Long)
    private Long updated_at; //修改时间
}
