package com.org.sendmail.mapper;


import com.org.sendmail.model.EmailStatue;

public interface EmailStatueRepository {

    EmailStatue findByTaskId(String taskId);
    void save(EmailStatue emailStatue);
}
