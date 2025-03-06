package com.org.emaildispatcher.mapper;


import com.org.emaildispatcher.model.EmailCustomer;

import java.util.ArrayList;
import java.util.List;

public interface EmailCustomerRepository {

    EmailCustomer findByEmail(String email);

    List<EmailCustomer> findAllCustomer();
}
