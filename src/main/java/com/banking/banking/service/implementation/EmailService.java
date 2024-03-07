package com.banking.banking.service.implementation;

import com.banking.banking.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlerts(EmailDetails emailDetails);

}
