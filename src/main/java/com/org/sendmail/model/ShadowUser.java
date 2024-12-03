package com.org.sendmail.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 影子邮件用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShadowUser implements Serializable {
    /**
     * 影子用户的id
     */
    private String id;
    /**
     * 影子用户的邮箱号
     */
    private String name;
    /**
     * 影子用户的授权码
     */
    private String authCode;
    /**
     * 影子用户的邮件服务器类型
     */
    private String host;
}
