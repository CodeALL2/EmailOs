package com.org.sendmail.mapper;

public interface EmailReportRepository {

    void addBounceAmountById(String email_task_id, int sum);
    void addDeliveryAmount(String email_task_id, int sum);
}
