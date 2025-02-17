package com.org.emaildispatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 邮件在 Elasticsearch 里的实体
 */
@Document(indexName = "email_paused") // 指定索引名称
public class EmailPaused implements Serializable {
    @Id
    private String id;
    @Field(type = FieldType.Integer)
    private String email_task_id;
}
