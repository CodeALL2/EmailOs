package com.org.sendmail.mapper;


import com.org.sendmail.model.EmailUser;

public interface EmailUserRepository {

    //根据 email 查询 EmailUser
    EmailUser findByEmail(String email);
}
