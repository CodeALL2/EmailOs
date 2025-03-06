package com.org.emaildispatcher.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.org.emaildispatcher.mapper.EmailUserRepository;
import com.org.emaildispatcher.model.EmailUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EmailUserRepositoryImp implements EmailUserRepository {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    public EmailUser findByEmail(String email) {
        try {
            // 构建查询条件
            Query query = Query.of(q -> q.term(t -> t.field("user_email").value(email)));

            // 执行查询
            SearchResponse<EmailUser> response = elasticsearchClient.search(
                    s -> s.index("user").query(query),
                    EmailUser.class
            );

            // 解析查询结果
            List<Hit<EmailUser>> hits = response.hits().hits();
            if (!hits.isEmpty()) {
                // 返回第一个匹配的用户信息
                return hits.get(0).source();
            } else {
                // 未找到用户，返回 null 或抛出异常
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户失败：" + e.getMessage());
        }
    }

    @Override
    public List<EmailUser> findAllBoos(Integer roleId) {
        try {
            SearchResponse<EmailUser> response = elasticsearchClient.search(s -> s
                            .index("user") // 索引名称
                            .query(q -> q
                                    .term(t -> t
                                            .field("user_role") // 查询 `user_role` 字段
                                            .value(roleId)
                                    )
                            ),
                    EmailUser.class
            );

            return response.hits().hits().stream()
                    .map(hit -> hit.source()) // 提取 source 数据
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Elasticsearch 状态异常，roleId: {}，错误信息: {}", roleId, e.getMessage(), e);
        }
        return null;
    }

}
