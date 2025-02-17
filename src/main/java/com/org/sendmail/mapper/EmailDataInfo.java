package com.org.sendmail.mapper;

import com.org.sendmail.model.EmailStatue;
import com.org.sendmail.model.UndeliveredEmail;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EmailDataInfo{
    void save(UndeliveredEmail undeliveredEmail);
}
