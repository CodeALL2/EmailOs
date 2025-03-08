package com.org.sendmail.mapper;

public interface EmailContentRepository {

    String findContentById(String email_task_id);
}
