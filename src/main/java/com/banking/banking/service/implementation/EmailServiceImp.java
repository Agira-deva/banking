package com.banking.banking.service.implementation;

import com.banking.banking.advice.EmailSendingException;
import com.banking.banking.dto.EmailDetails;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;

import static com.mysql.cj.conf.PropertyKey.logger;

@Service
@Slf4j
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

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) throws MessagingException {
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try{
            mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setText(emailDetails.getMessageBody());
            mimeMessageHelper.setSubject(emailDetails.getSubject());

            FileSystemResource file=new FileSystemResource(new File(emailDetails.getAttachment()));
            mimeMessageHelper.addAttachment(file.getFilename(), file);
            javaMailSender.send(mimeMessage);

            log.info(file.getFilename()+"has been sent to user with email"+emailDetails.getRecipient());

        } catch (MessagingException messagingException) {
            log.error("MessagingException occurred while sending email: " + messagingException.getMessage());
            throw new EmailSendingException("Error occurred while sending email", messagingException);
        } catch (MailAuthenticationException mailAuthenticationException) {
            log.error("AuthenticationFailedException occurred while sending email: " + mailAuthenticationException.getMessage());
            throw new EmailSendingException("Authentication failed while sending email", mailAuthenticationException);
        } catch (MailSendException mailSenderException) {
            log.error("SendFailedException occurred while sending email: " + mailSenderException.getMessage());
            throw new EmailSendingException("Failed to send email", mailSenderException);
        } catch (Exception exception) {
            log.error("Exception occurred while sending email: " + exception.getMessage());
            throw new EmailSendingException("An error occurred while sending email", exception);
        }
    }
}
