package com.org.sendmail.mapper.imp;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.org.sendmail.mapper.EmailDataInfo;
import com.org.sendmail.model.UndeliveredEmail;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailDataInfoImp implements EmailDataInfo {

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Override
    public void save(UndeliveredEmail undeliveredEmail) {
        try {
            IndexResponse response = elasticsearchClient.index(IndexRequest.of(i -> i
                    .index("email_details") // 指定索引名称
                    .id(undeliveredEmail.getEmail_id()) // 指定主键
                    .document(undeliveredEmail) // 插入的文档
            ));

            System.out.println("Document indexed with ID: " + response.id());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to save document: " + e.getMessage());
        }
    }
}
