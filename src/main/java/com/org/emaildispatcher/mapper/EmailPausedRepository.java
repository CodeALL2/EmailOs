package com.org.emaildispatcher.mapper;

import com.org.emaildispatcher.model.EmailPaused;
import com.org.emaildispatcher.model.EmailStatue;
import com.org.emaildispatcher.model.EmailTask;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

public interface EmailPausedRepository{

    EmailPaused findByTaskId(String taskId);

    void save(EmailPaused emailTask);
}
