package com.banking.banking.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class Bankingexception {

//    @ExceptionHandler(InsufficientFundsException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> handleInsufficientFunds(InsufficientFundsException ex) {
//        Map<String, String> errorMap = new HashMap<>();
//        errorMap.put("error", ex.getMessage());
//        return errorMap;
//    }


}
