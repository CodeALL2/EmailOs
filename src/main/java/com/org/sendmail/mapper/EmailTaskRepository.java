package com.org.sendmail.mapper;


import com.org.sendmail.model.EmailTask;

public interface EmailTaskRepository {

//    @Query("{\"match\": {\"email_task_id\": \"?0\"}}")
      EmailTask findByTaskId(String taskId);

      void save(EmailTask emailTask);
}