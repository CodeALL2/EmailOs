package com.org.sendmail.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import com.org.sendmail.mapper.EmailUserRepository;
import com.org.sendmail.model.EmailUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class EmailUserRepositoryImp implements EmailUserRepository {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public EmailUser findByEmail(String email) {
        try {
            // 构建查询请求
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("email_user") // 索引名称
                    .query(Query.of(q -> q
                            .term(t -> t
                                    .field("user_email") // 字段名称
                                    .value(email)       // 查询的值
                            )
                    ))
            );

            // 执行查询
            SearchResponse<EmailUser> response = elasticsearchClient.search(searchRequest, EmailUser.class);

            // 处理返回结果
            List<Hit<EmailUser>> hits = response.hits().hits();
            if (!hits.isEmpty()) {
                // 返回第一个匹配的结果
                return hits.get(0).source();
            }
        } catch (IOException e) {
            e.printStackTrace(); // 打印错误日志
        }

        // 如果没有匹配结果或发生错误，返回 null
        return null;
    }
}
