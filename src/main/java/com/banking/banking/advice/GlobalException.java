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
    @ExceptionHandler(BalanceNotSufficientException.class)
    public ResponseEntity<String> balanceNotSufficientException(BalanceNotSufficientException balanceNotSufficientException){
        return new ResponseEntity<>(balanceNotSufficientException.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> invalidInputException(InvalidInputException invalidInputException){
        return new ResponseEntity<>(invalidInputException.getMessage(), HttpStatus.NOT_FOUND);
    }

}
