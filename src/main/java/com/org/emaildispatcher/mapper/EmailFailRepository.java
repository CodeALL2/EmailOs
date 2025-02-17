package com.org.emaildispatcher.mapper;


import com.org.emaildispatcher.model.EmailFail;

public interface EmailFailRepository {

    EmailFail findById(String emailTaskId);

    void save(EmailFail emailFail);
}
