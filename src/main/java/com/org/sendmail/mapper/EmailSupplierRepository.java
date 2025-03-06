package com.org.sendmail.mapper;


import com.org.sendmail.model.EmailSupplier;
import com.org.sendmail.model.EmailUser;

import java.util.List;

public interface EmailSupplierRepository {

    EmailSupplier findByEmail(String email);

    List<EmailSupplier> findAll();

}
