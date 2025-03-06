package com.org.emaildispatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailCustomer {
    private String contactPerson;

    private String contactWay;

    private Integer tradeType;

    private String sex;

    private String birth;

    private Integer status;

    private String createdAt;

    private String updatedAt;

    private List<String> emails;

    private String customerId;

    private String customerName;

    private Integer customerLevel;

    private String creatorId;

    private String customerCountryId;

    private List<String> commodityId;

    private List<String> acceptEmailTypeId;

    private String belongUserId;
}
