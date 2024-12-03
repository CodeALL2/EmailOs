package com.org.sendmail.model;

import java.io.Serializable;

/**
 * 普通邮件用户
 */
public class EmailUser implements Serializable {
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
