package com.banking.banking.Utility;

import java.time.Year;

public class AccountUtility {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "A user already has an account created! ";
    public static final String ACCOUNT_CREATED_SUCCESS = "002";
    public static final String ACCOUNT_CREATED_MESSAGE = " Account has been successfully created ";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = " User with provided Account number does not exist ";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_FOUND_MESSAGE = " User  found ";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = " User Account has been credited  ";
    public static final String ACCOUNT_CREDITED_CODE = "005";
    public static final String INSUFFICIENT_MESSAGE = " Oops ! Insufficient Balance  ";
    public static final String INSUFFICIENT_CODE = "006";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = " Account has been  successfully debited  ";
    public static final String ACCOUNT_DEBITED_CODE = "007";
    public static final String TRANSFER_SUCCESS_CODE="008";
    public static final String TRANSFER_SUCCESS_MESSAGE="Transfer successfully completed ";


    public static String generateAccountNumber() {
        //current year * sixdigits
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + (min));

        String year = String.valueOf(currentYear);
        String number = String.valueOf(randomNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();

    }

}
