package com.org.emaildispatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailSupplier {

    private String supplierId;

    private String supplierName;

    private String contactPerson;

    private String contactWay;

    private Integer supplierLevel;

    private Integer tradeType;

    private String sex;

    private String birth;

    private Integer status;

    private String createdAt;

    private String updatedAt;

    private List<String> emails;

    private String creatorId;

    private String supplierCountryId;

    private List<String> commodityId;

    private List<String> acceptEmailTypeId;

    private String belongUserId;
}
