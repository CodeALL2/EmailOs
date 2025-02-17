package com.org.emaildispatcher.mapper;

import com.org.emaildispatcher.model.EmailStatue;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

public interface EmailStatueRepository{

    EmailStatue findByTaskId(String taskId);
    void save(EmailStatue emailStatue);
}
