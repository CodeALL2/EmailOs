package com.org.emaildispatcher.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.org.emaildispatcher.mapper.EmailStatueRepository;
import com.org.emaildispatcher.model.EmailStatue;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class EmailStatueRepositoryImp implements EmailStatueRepository {

    @Resource
    private ElasticsearchClient elasticsearchClient;
    @Override
    public EmailStatue findByTaskId(String taskId) {
        try {
            // 构建查询条件
            Query query = Query.of(q -> q.term(t -> t.field("email_task_id").value(taskId)));

            // 执行查询
            SearchResponse<EmailStatue> response = elasticsearchClient.search(
                    s -> s.index("email_statue").query(query),
                    EmailStatue.class
            );

            // 解析查询结果
            List<Hit<EmailStatue>> hits = response.hits().hits();
            if (!hits.isEmpty()) {
                // 返回第一个匹配的任务状态
                return hits.get(0).source();
            } else {
                // 如果未找到结果，返回 null 或抛出异常
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询邮件任务状态失败：" + e.getMessage());
        }
    }

    @Override
    public void save(EmailStatue emailStatue) {
        try {
            // 如果没有ID，生成一个
            if (emailStatue.getEmail_id() == null || emailStatue.getEmail_id().isEmpty()) {
                emailStatue.setEmail_id(UUID.randomUUID().toString());
            }

            // 设置时间戳
            long currentTime = System.currentTimeMillis();
            if (emailStatue.getCreated_at() == null) {
                emailStatue.setCreated_at(currentTime);
            }
            emailStatue.setUpdate_at(currentTime);

            // 验证状态值
            if (emailStatue.getEmail_status() == null ||
                    emailStatue.getEmail_status() < 1 ||
                    emailStatue.getEmail_status() > 6) {
                throw new IllegalArgumentException("无效的邮件状态值");
            }

            // 构建索引请求
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("email_statue")
                    .id(emailStatue.getEmail_id())
                    .document(emailStatue)
                    .refresh(Refresh.True)  // 立即刷新，使文档可搜索
            );

            // 检查响应状态
            if (response.result().name().equals("CREATED")) {
                log.info("邮件状态创建成功，id: {}, task_id: {}, status: {}",
                        emailStatue.getEmail_id(),
                        emailStatue.getEmail_task_id(),
                        emailStatue.getEmail_status());
            } else if (response.result().name().equals("UPDATED")) {
                log.info("邮件状态更新成功，id: {}, task_id: {}, status: {}",
                        emailStatue.getEmail_id(),
                        emailStatue.getEmail_task_id(),
                        emailStatue.getEmail_status());
            } else {
                log.warn("邮件状态保存返回未知状态，id: {}, status: {}",
                        emailStatue.getEmail_id(), response.result().name());
            }

        } catch (ElasticsearchException e) {
            log.error("保存邮件状态失败，task_id: {}, error: {}",
                    emailStatue.getEmail_task_id(), e.getMessage(), e);
        } catch (IOException e) {
            log.error("保存邮件状态时发生IO异常，task_id: {}",
                    emailStatue.getEmail_task_id(), e);
        }
    }
}
