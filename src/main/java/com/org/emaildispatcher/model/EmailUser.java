package com.org.emaildispatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 普通邮件用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailUser implements Serializable, IUser {
    /**
     * 普通用户的id
     */
    private String id;
    /**
     * 普通用户的邮箱号
     */
    private String email;
    /**
     * 普通用户的授权码
     */
    private String authCode;
    /**
     * 普通用户的邮件服务器类型
     */
    private String host;
}
