package com.org.sendmail.mapper.imp;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetResponse;
import com.org.sendmail.mapper.EmailCountryRepository;
import com.org.sendmail.model.EmailCountry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class EmailCountryRepositoryImp implements EmailCountryRepository {
    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public String mapCountry(String countryId) {
        try {
            GetResponse<EmailCountry> response = elasticsearchClient.get(g -> g
                            .index("country") // 索引名
                            .id(countryId),   // 直接用 countryId 作为 _id
                    EmailCountry.class
            );

            if (response.found()) {
                return response.source().getCountry_name(); // 返回 country_name
            } else {
                log.warn("未找到 country_id: {}", countryId);
                return null;
            }

        } catch (ElasticsearchException e) {
            log.error("Elasticsearch 查询错误，country_id: {}，错误信息: {}", countryId, e.getMessage(), e);
            throw new RuntimeException("Elasticsearch 查询失败", e);
        } catch (IOException e) {
            log.error("IO 异常，查询失败，country_id: {}，错误信息: {}", countryId, e.getMessage(), e);
            throw new RuntimeException("查询任务失败", e);
        }
    }

}
