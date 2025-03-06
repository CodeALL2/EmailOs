package com.org.sendmail.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.org.sendmail.mapper.EmailCustomerRepository;
import com.org.sendmail.model.EmailCustomer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

//    @Override
//    public List<EmailCustomer> findAllCustomer(Long nowTime) {
//        try {
//
//            // 计算 24 小时后的阈值（秒）
//            long threshold = nowTime;
//
//            SearchResponse<EmailCustomer> response = elasticsearchClient.search(s -> s
//                            .index("customer")
//                            .query(q -> q
//                                    .range(r -> r
//                                            .field("birth")
//                                            .lt(JsonData.of(threshold)) // 直接比较long数值
//                                    )
//                            ),
//                    EmailCustomer.class
//            );
//
//            return response.hits().hits().stream()
//                    .map(hit -> hit.source())
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//
//        } catch (Exception e) {
//            log.error("Elasticsearch 查询异常，错误信息: {}", e.getMessage(), e);
//        }
//        return null;
//    }

    @Override
    public List<EmailCustomer> findAllCustomer() {
        try {
            // 获取今天的 MM-dd 格式
            LocalDate today = LocalDate.now();
            int month = today.getMonthValue();
            int day = today.getDayOfMonth();

            System.out.println("查询条件: " + month + "-" + day);

            // 查询 ES
            SearchResponse<EmailCustomer> response = elasticsearchClient.search(s -> s
                            .index("customer")
                            .query(q -> q
                                    .script(script -> script
                                            .script(code -> code
                                                    .inline(inline -> inline
                                                            .lang("painless") // 指定 Painless 作为脚本语言
                                                            .source("doc['birth'].value.getMonthValue() == params.month && doc['birth'].value.getDayOfMonth() == params.day")
                                                            .params("month", JsonData.of(month))
                                                            .params("day", JsonData.of(day))
                                                    )
                                            )
                                    )
                            ),
                    EmailCustomer.class
            );

            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Elasticsearch 查询异常，错误信息: {}", e.getMessage(), e);
        }
        return null;
    }


}
