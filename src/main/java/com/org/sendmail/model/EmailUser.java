package com.org.sendmail.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 普通邮件用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "email_user") // 指定索引名称
public class EmailUser implements Serializable{
    /**
     * 普通用户的id
     */
    @Id // 标记为主键
    private String user_id;

    private Integer user_role; //用户角色

    private String creator_id; //创建人id

    private String belong_user_id; //所属用户id

    private String user_name; //用户名

    private String user_account; //用户登录账号

    private String user_password; //用户的密码

    private String user_email; //用户的邮箱

    private String user_email_code; //用户的邮箱授权码

    private String user_host;// 用户邮箱的类型

    private ArrayList<String> user_auth_id; //用户权限id

    private Integer status; //用户分配状态

    private Long created_at; //任务创建时间

    private Long update_at; //修改时间

}
