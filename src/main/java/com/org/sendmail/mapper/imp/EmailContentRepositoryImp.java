package com.org.sendmail.mapper.imp;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.org.sendmail.mapper.EmailContentRepository;
import com.org.sendmail.model.EmailContent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class EmailContentRepositoryImp implements EmailContentRepository {

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public String findContentById(String email_task_id) {
        try {
            GetResponse<EmailContent> response = elasticsearchClient.get(g -> g
                            .index("email_content") // 索引名
                            .id(email_task_id),     // 直接用 email_task_id 作为 _id
                    EmailContent.class
            );

            if (response.found()) {
                return response.source().getEmail_content(); // 返回 email_content
            } else {
                log.warn("未找到 email_task_id: {}", email_task_id);
                return null;
            }

        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 查询错误，email_task_id: {}，错误信息: {}", email_task_id, e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 查询失败", e);
        } catch (IOException e) {
            log.error("IO 异常，查询失败，email_task_id: {}，错误信息: {}", email_task_id, e.getMessage(), e);
            throw new RuntimeException("查询任务失败", e);
        }
    }

}
