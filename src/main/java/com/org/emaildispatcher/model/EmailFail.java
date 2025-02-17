package com.org.emaildispatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


/**
 * 邮件失败表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "email_fail")
public class EmailFail {
    //邮件重发id
    @Id
    private String email_resend_id; //邮件任务id

    @Field(type = FieldType.Keyword)
    private String email_task_id; //邮件任务id

    @Field(type = FieldType.Keyword)
    private String accepter_email; //接受者的邮箱

    @Field(type = FieldType.Long)
    private Long statue = 0L; //默认未重发

    @Field(type = FieldType.Text)
    private String error_msg; //重发错误信息

    @Field(type = FieldType.Long)
    private Long start_time; //重发开始时间

    @Field(type = FieldType.Long)
    private Long end_time; //重发完成时间
}
