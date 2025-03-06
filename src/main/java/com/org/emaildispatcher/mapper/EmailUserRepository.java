package com.org.emaildispatcher.mapper;

import com.org.emaildispatcher.model.EmailUser;
import org.springframework.data.elasticsearch.annotations.Query;

import java.util.List;

public interface EmailUserRepository{

    //根据 email 查询 EmailUser
    EmailUser findByEmail(String email);

    //查询所有大管理
    List<EmailUser> findAllBoos(Integer roleId);
}
