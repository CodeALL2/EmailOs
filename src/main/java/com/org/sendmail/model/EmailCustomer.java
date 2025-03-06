package com.org.sendmail.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailCustomer {
    private String contact_person;

    private String contact_way;

    private Integer trade_type;

    private String sex;

    private String birth;

    private Integer status;

    private String created_at;

    private String updated_at;

    private List<String> emails;

    private String customer_id;

    private String customer_name;

    private Integer customer_level;

    private String creator_id;

    private String customer_country_id;

    private List<String> commodity_id;

    private List<String> acceptEmailTypeId;

    private String belong_user_id;
}
