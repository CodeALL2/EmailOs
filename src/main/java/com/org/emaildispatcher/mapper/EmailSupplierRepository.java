package com.org.emaildispatcher.mapper;


import com.org.emaildispatcher.model.EmailSupplier;

import java.util.List;

public interface EmailSupplierRepository {

    EmailSupplier findByEmail(String email);

    List<EmailSupplier> findAll();
}
