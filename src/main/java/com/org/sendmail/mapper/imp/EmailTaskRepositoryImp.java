package com.org.sendmail.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import com.org.sendmail.mapper.EmailTaskRepository;
import com.org.sendmail.model.EmailTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class EmailTaskRepositoryImp implements EmailTaskRepository {

    @Resource
    private ElasticsearchClient elasticsearchClient;

    //@Query("{\"match\": {\"email_task_id\": \"?0\"}}")
    @Override
    public EmailTask findByTaskId(String taskId) {
        try {
            SearchResponse<EmailTask> response = elasticsearchClient.search(s -> s
                            .index("email_task")  // 替换为您的实际索引名称
                            .query(q -> q
                                    .match(m -> m
                                            .field("email_task_id")
                                            .query(taskId)
                                    )
                            ),
                    EmailTask.class
            );

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .findFirst()
                    .orElse(null);

        } catch (IOException e) {
            log.error("查询任务失败，taskId: {}", taskId, e);
            throw new RuntimeException("查询任务失败", e);
        }
    }

    @Override
    public void save(EmailTask emailTask) {
        try {
            // 如果没有ID，生成一个
            if (emailTask.getEmail_id() == null || emailTask.getEmail_id().isEmpty()) {
                emailTask.setEmail_id(UUID.randomUUID().toString());
            }

            // 如果没有创建时间，设置当前时间
            if (emailTask.getCreated_at() == null) {
                emailTask.setCreated_at(System.currentTimeMillis() / 1000);
            }

            // 构建索引请求
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("email_task")  // 索引名称
                    .id(emailTask.getEmail_id())
                    .document(emailTask)
                    .refresh(Refresh.True)  // 立即刷新，使文档可搜索
            );

            // 检查响应状态
            if (response.result().name().equals("CREATED")) {
                log.info("邮件任务创建成功，id: {}, task_id: {}",
                        emailTask.getEmail_id(), emailTask.getEmail_task_id());
            } else if (response.result().name().equals("UPDATED")) {
                log.info("邮件任务更新成功，id: {}, task_id: {}",
                        emailTask.getEmail_id(), emailTask.getEmail_task_id());
            } else {
                log.warn("邮件任务保存返回未知状态，id: {}, status: {}",
                        emailTask.getEmail_id(), response.result().name());
            }

        } catch (ElasticsearchException e) {
            log.error("保存邮件任务到ES失败，task_id: {}, error: {}",
                    emailTask.getEmail_task_id(), e.getMessage(), e);
        } catch (IOException e) {
            log.error("保存邮件任务时发生IO异常，task_id: {}",
                    emailTask.getEmail_task_id(), e);
        }
    }


}
