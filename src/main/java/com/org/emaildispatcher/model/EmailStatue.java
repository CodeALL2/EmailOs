package com.org.emaildispatcher.model;

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
@Document(indexName = "email_statue") // 指定索引名称
public class EmailStatue {
    @Id
    private String email_id; //主键

    @Field(type = FieldType.Keyword)
    private String email_task_id; //邮件任务的id

    @Field(type = FieldType.Integer)
    private Integer email_status; //邮件状态 使用数字1开始 2暂停 3终止 4重置 5完成 6异常

    @Field(type = FieldType.Long)
    private Long created_at; //创建时间

    @Field(type = FieldType.Long)
    private Long update_at; //修改时间
}
