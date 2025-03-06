package com.org.sendmail.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.json.JsonData;
import com.org.sendmail.mapper.EmailReportRepository;
import com.org.sendmail.model.EmailReport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class EmailReportRepositoryImp implements EmailReportRepository {

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public void addBounceAmountById(String email_task_id, int sum) {
        try {
            UpdateResponse<EmailReport> response = elasticsearchClient.update(u -> u
                            .index("email_report") // ES 索引名
                            .id(email_task_id) // 文档 ID
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless")
                                            .source("ctx._source.bounce_amount = params.sum") // 直接设置为 sum
                                            .params(Map.of("sum", JsonData.of(sum))) // 传递参数 sum
                                    )
                            ),
                    EmailReport.class
            );

            if ("UPDATED".equals(response.result().name())) {
                log.info("成功更新 email_task_id: {}, 新的 bounce_amount: {}", email_task_id, sum);
            } else {
                log.warn("未找到 email_task_id: {}", email_task_id);
            }

        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 更新错误，email_task_id: {}，错误信息: {}", email_task_id, e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 更新失败", e);
        } catch (IOException e) {
            log.error("IO 异常，更新失败，email_task_id: {}，错误信息: {}", email_task_id, e.getMessage(), e);
            throw new RuntimeException("更新任务失败", e);
        }
    }


    @Override
    public void addDeliveryAmount(String email_task_id, int sum) {
        try {
            UpdateResponse<EmailReport> response = elasticsearchClient.update(u -> u
                            .index("email_report") // ES 索引名
                            .id(email_task_id) // 文档 ID
                            .script(s -> s
                                    .inline(i -> i
                                            .lang("painless") // Painless 脚本
                                            .source("ctx._source.delivery_amount = params.sum") // 直接赋值
                                            .params(Map.of("sum", JsonData.of(sum))) // 传递参数 sum
                                    )
                            ),
                    EmailReport.class
            );

            if ("UPDATED".equals(response.result().name())) {
                log.info("成功更新 delivery_amount，email_task_id: {}，新值: {}", email_task_id, sum);
            } else {
                log.warn("未找到 email_task_id: {}", email_task_id);
            }

        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 更新错误，email_task_id: {}，错误信息: {}", email_task_id, e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 更新失败", e);
        } catch (IOException e) {
            log.error("IO 异常，更新失败，email_task_id: {}，错误信息: {}", email_task_id, e.getMessage(), e);
            throw new RuntimeException("更新任务失败", e);
        }
    }


}
