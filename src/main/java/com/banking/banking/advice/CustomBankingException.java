package com.banking.banking.advice;

import jdk.jshell.spi.ExecutionControl;

public class CustomBankingException extends Exception{

//   private final message;
     public CustomBankingException(String message){
         super(message);
     }


}
