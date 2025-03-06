package com.org.emaildispatcher.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.org.emaildispatcher.mapper.EmailCustomerRepository;
import com.org.emaildispatcher.model.EmailCustomer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EmailCustomerRepositoryImp implements EmailCustomerRepository {

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public EmailCustomer findByEmail(String email) {
        try {
            SearchResponse<EmailCustomer> response = elasticsearchClient.search(s -> s
                            .index("customer") // 索引名称
                            .query(q -> q
                                    .term(t -> t
                                            .field("emails") // 这里查询 `emails` 数组字段
                                            .value(email)
                                    )
                            ),
                    EmailCustomer.class
            );

            if (response.hits().hits().isEmpty()) {
                log.warn("未找到邮箱: {}", email);
                return null;
            }

            return response.hits().hits().get(0).source(); // 返回第一个匹配的结果

        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 查询错误，email: {}，错误信息: {}", email, e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 查询失败", e);
        } catch (IOException e) {
            log.error("IO 异常，查询失败，email: {}，错误信息: {}", email, e.getMessage(), e);
            throw new RuntimeException("查询任务失败", e);
        }
    }

    @Override
    public List<EmailCustomer> findAllCustomer() {
        try {
            SearchResponse<EmailCustomer> response = elasticsearchClient.search(s -> s
                            .index("customer")  // 索引名称
                            .query(q -> q.matchAll(m -> m)), // matchAll 查询
                    EmailCustomer.class
            );

            return response.hits().hits().stream()
                    .map(hit -> hit.source()) // 提取 source 数据
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Elasticsearch 状态异常，错误信息: {}", e.getMessage(), e);
        }
        return null;
    }


}
