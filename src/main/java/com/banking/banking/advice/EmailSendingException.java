package com.banking.banking.advice;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailSendingException extends RuntimeException{
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }


}
