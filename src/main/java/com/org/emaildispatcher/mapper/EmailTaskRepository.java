package com.org.emaildispatcher.mapper;


import com.org.emaildispatcher.model.EmailTask;
import org.springframework.stereotype.Repository;

public interface EmailTaskRepository{

//    @Query("{\"match\": {\"email_task_id\": \"?0\"}}")
      EmailTask findByTaskId(String taskId);

      void save(EmailTask emailTask);
}