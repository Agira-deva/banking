package com.banking.banking.service.implementation;

import com.banking.banking.dto.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmailAlerts(EmailDetails emailDetails) {
     try{
         SimpleMailMessage mailMessage=new SimpleMailMessage();
         mailMessage.setFrom(senderEmail);
         mailMessage.setTo(emailDetails.getRecipient());
         mailMessage.setText(emailDetails.getMessageBody());
         mailMessage.setSubject(emailDetails.getSubject());
         javaMailSender.send(mailMessage);
         System.out.println("mail sent successfully");
     }catch (MailException exception){
         throw new RuntimeException(exception);
     }
    }
}
