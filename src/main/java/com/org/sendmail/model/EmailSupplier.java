package com.org.sendmail.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailSupplier {

    private String supplier_id;

    private String supplier_name;

    private String contact_person;

    private String contact_way;

    private Integer supplier_level;

    private Integer trade_type;

    private String sex;

    private String birth;

    private Integer status;

    private String created_at;

    private String updated_at;

    private List<String> emails;

    private String creator_id;

    private String supplier_country_id;

    private List<String> commodity_id;

    private List<String> acceptEmailTypeId;

    private String belong_user_id;
}
