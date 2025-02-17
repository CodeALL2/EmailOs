package com.org.emaildispatcher.mapper;

import com.org.emaildispatcher.model.EmailUser;
import org.springframework.data.elasticsearch.annotations.Query;

public interface EmailUserRepository{

    //根据 email 查询 EmailUser
    EmailUser findByEmail(String email);
}
