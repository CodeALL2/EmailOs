package com.org.emaildispatcher.util;


import com.org.emaildispatcher.model.EmailUser;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ElasticSearchUtil {

    @Autowired
    private RestHighLevelClient client;

    public Boolean createIndex(String indexName) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.mapping(
                "{\n" +
                        "  \"properties\": {\n" +
                        "    \"message\": {\n" +
                        "      \"type\": \"text\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                XContentType.JSON);
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据邮箱名进行精确查找
     * @param indexName 索引库
     * @param fieldName 查找字段
     * @param value 精确值
     * @return ShadowUser
     */
    public EmailUser searchEmailsByEmailName(String indexName, String fieldName, String value) {
        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest(indexName);

        // 创建查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 使用 termQuery 进行精确查询
        sourceBuilder.query(QueryBuilders.termQuery(fieldName, value));

        // 将查询条件添加到搜索请求中
        searchRequest.source(sourceBuilder);

        // 执行查询
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            // 处理查询结果
            List<EmailUser> result = new ArrayList<>();
            for (SearchHit hit : hits) {
                // 将每个命中的结果转换为 Map 类型
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                // 将 Map 转换为 Email 对象
                EmailUser email = new EmailUser();
                email.setId(hit.getId());
                email.setHost((String) sourceAsMap.get("host"));
                email.setAuthCode((String) sourceAsMap.get("auth_code"));
                email.setEmail((String) sourceAsMap.get("name"));
                // 将 Email 对象添加到结果列表
                result.add(email);
            }
            return result.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // 如果查询失败，返回空列表
    }

    /**
     * 根据id进行查找操作
     * @param indexName 索引库
     * @param documentId 唯一id
     * @return ShadowUser
     */
    public EmailUser getDocumentById(String indexName, String documentId) {
        // 创建 GetRequest 请求对象
        GetRequest getRequest = new GetRequest(indexName, "_doc", documentId);

        try {
            // 执行查询
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

            // 检查文档是否存在
            if (getResponse.isExists()) {
                // 获取文档的源数据
                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();

                // 将文档源数据映射到 ShadowUser 对象
                EmailUser user = new EmailUser();
                user.setId(documentId);  // 设置文档 ID
                user.setHost((String) sourceAsMap.get("host"));
                user.setAuthCode((String) sourceAsMap.get("auth_code"));
                user.setEmail((String) sourceAsMap.get("name"));

                // 返回结果
                return user;
            } else {
                System.out.println("Document not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // 如果获取失败，返回 null
    }


}
