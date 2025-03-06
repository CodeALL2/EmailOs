package com.org.sendmail.mapper;

import com.org.sendmail.model.EmailCustomer;
import com.org.sendmail.model.EmailFail;

import java.util.List;

public interface EmailCustomerRepository {

    EmailCustomer findByEmail(String email);

    List<EmailCustomer> findAllCustomer();
}
