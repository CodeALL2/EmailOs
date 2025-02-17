package com.org.emaildispatcher.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.org.emaildispatcher.mapper.EmailPausedRepository;
import com.org.emaildispatcher.model.EmailPaused;
import com.org.emaildispatcher.model.EmailTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class EmailPausedRepositoryImp implements EmailPausedRepository {

    @Resource
    private ElasticsearchClient elasticsearchClient;
    @Override
    public EmailPaused findByTaskId(String taskId) {
        try {
            // 构建查询条件
            Query query = Query.of(q -> q.term(t -> t.field("email_task_id").value(taskId)));

            // 执行查询
            SearchResponse<EmailPaused> response = elasticsearchClient.search(
                    s -> s.index("email_paused").query(query),
                    EmailPaused.class
            );

            // 解析查询结果
            List<Hit<EmailPaused>> hits = response.hits().hits();
            if (!hits.isEmpty()) {
                // 返回第一个匹配的暂停任务
                return hits.get(0).source();
            } else {
                // 未找到对应的暂停任务，返回 null 或抛出异常
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void save(EmailPaused emailPaused) {
        try {
            // 如果没有ID，生成一个
            if (emailPaused.getId() == null || emailPaused.getId().isEmpty()) {
                emailPaused.setId(UUID.randomUUID().toString());
            }

            // 构建索引请求
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("email_paused")  // 使用 email_paused 索引
                    .id(emailPaused.getId())
                    .document(emailPaused)
                    .refresh(Refresh.True)  // 立即刷新，使文档可搜索
            );

            // 检查响应状态
            if (response.result().name().equals("CREATED")) {
                log.info("暂停邮件任务创建成功，id: {}, task_id: {}",
                        emailPaused.getId(), emailPaused.getEmail_task_id());
            } else if (response.result().name().equals("UPDATED")) {
                log.info("暂停邮件任务更新成功，id: {}, task_id: {}",
                        emailPaused.getId(), emailPaused.getEmail_task_id());
            } else {
                log.warn("暂停邮件任务保存返回未知状态，id: {}, status: {}",
                        emailPaused.getId(), response.result().name());
            }

        } catch (ElasticsearchException e) {
            log.error("保存暂停邮件任务失败，task_id: {}, error: {}",
                    emailPaused.getEmail_task_id(), e.getMessage(), e);
        } catch (IOException e) {
            log.error("保存暂停邮件任务时发生IO异常，task_id: {}",
                    emailPaused.getEmail_task_id(), e);
        }
    }
}
