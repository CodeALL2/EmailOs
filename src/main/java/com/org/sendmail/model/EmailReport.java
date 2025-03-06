package com.org.sendmail.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "email_report") // 指定索引名称
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailReport {
    /**
     * 退信数量
     */
    private long bounce_amount;
    /**
     * 送达数量
     */
    private long delivery_amount;
    /**
     * 任务id
     */
    private String email_task_id;
    /**
     * 具体邮件总数
     */
    private long email_total;
    /**
     * 打开数量
     */
    private long open_amount;

    private long unsubscribe_amount;
}
