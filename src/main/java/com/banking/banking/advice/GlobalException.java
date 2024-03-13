package com.banking.banking.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(CustomBankingException.class)
    public ResponseEntity<String> customerBankingException(CustomBankingException customBankingException){
        return new ResponseEntity<>(customBankingException.getMessage(), HttpStatus.NOT_FOUND);
    }


}
