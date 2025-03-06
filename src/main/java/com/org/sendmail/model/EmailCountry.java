package com.org.sendmail.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailCountry {
    /**
     * 国家代码，例如中国是zh、美国是us
     */
    private String country_code;
    /**
     * 国家id，使用uuid
     */
    private String country_id;
    /**
     * 国家名称
     */
    private String country_name;
    /**
     * 创建时间
     */
    private String created_at;
    /**
     * 更新时间
     */
    private String updated_at;
}
