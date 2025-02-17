package com.org.emaildispatcher.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 普通邮件用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "email_user") // 指定索引名称
@JsonIgnoreProperties(ignoreUnknown=true)
public class EmailUser implements Serializable{
    /**
     * 普通用户的id
     */
    @Id // 标记为主键
    private String user_id;

    @Field(type = FieldType.Integer)
    private Integer user_role; //用户角色

    @Field(type = FieldType.Keyword)
    private String creator_id; //创建人id

    @Field(type = FieldType.Keyword)
    private String belong_user_id; //所属用户id

    @Field(type = FieldType.Keyword)
    private String user_name; //用户名

    @Field(type = FieldType.Keyword)
    private String user_account; //用户登录账号

    @Field(type = FieldType.Keyword)
    private String user_password; //用户的密码

    @Field(type = FieldType.Keyword)
    private String user_email; //用户的邮箱

    @Field(type = FieldType.Keyword)
    private String user_email_code; //用户的邮箱授权码

    @Field(type = FieldType.Keyword)
    private String user_host;// 用户邮箱的类型

    @Field(type = FieldType.Keyword)
    private List<String> user_auth_id; //用户权限id

    @Field(type = FieldType.Integer)
    private Integer status; //用户分配状态

    @Field(type = FieldType.Long)
    private Long created_at; //任务创建时间

    @Field(type = FieldType.Long)
    private Long update_at; //修改时间

}
