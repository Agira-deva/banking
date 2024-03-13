package com.banking.banking.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BalanceNotSufficientException extends Exception{

    public BalanceNotSufficientException(String message){
        super(message);
    }
}
