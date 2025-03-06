package com.org.emaildispatcher.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;

import com.org.emaildispatcher.mapper.EmailFailRepository;
import com.org.emaildispatcher.model.EmailFail;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class EmailFailImp implements EmailFailRepository {

    @Resource
    private ElasticsearchClient elasticsearchClient;


    @Override
    public EmailFail findById(String id) {
        try {
            GetResponse<EmailFail> response = elasticsearchClient.get(g -> g
                            .index("resend_details")  // 指定索引名称
                            .id(id),  // 按 `_id` 查询
                    EmailFail.class
            );

            if (response.found()) {
                return response.source();
            } else {
                log.warn("未找到文档，id: {}", id);
                return null;
            }

        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 查询错误，id: {}", id, e);
            throw new RuntimeException("Elasticsearch 查询失败", e);
        } catch (IOException e) {
            log.error("IO 异常，查询失败，id: {}", id, e);
            throw new RuntimeException("查询任务失败", e);
        }
    }




    @Override
    public void save(EmailFail emailFail) {
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("email_details")  // 索引名称
                    .id(emailFail.getEmail_resend_id())  // 使用 email_resend_id 作为主键
                    .document(emailFail)  // 存入的对象
            );

            log.info("保存成功，索引状态: {}", response.result().name());
        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 存储错误: {}", emailFail, e);
            throw new RuntimeException("Elasticsearch 存储失败", e);
        } catch (IOException e) {
            log.error("IO 异常，保存失败: {}", emailFail, e);
            throw new RuntimeException("保存任务失败", e);
        }
    }

}
