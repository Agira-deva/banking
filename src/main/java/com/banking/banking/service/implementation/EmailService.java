package com.banking.banking.service.implementation;

import com.banking.banking.dto.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailAlerts(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails) throws MessagingException;
}
