package com.org.sendmail.mapper;

import com.org.sendmail.model.EmailFail;

public interface EmailFailRepository {

    EmailFail findById(String emailTaskId);

    void save(EmailFail emailFail);
}
