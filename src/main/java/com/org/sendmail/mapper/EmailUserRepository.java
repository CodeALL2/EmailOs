package com.org.sendmail.mapper;


import com.org.sendmail.model.EmailUser;

import java.util.List;

public interface EmailUserRepository {

    //根据 email 查询 EmailUser
    EmailUser findByEmail(String email);

    List<EmailUser> findAllBoos(Integer roleId);
}
