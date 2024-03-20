package com.banking.banking.service.implementation;

import com.banking.banking.advice.BalanceNotSufficientException;
import com.banking.banking.dto.*;
import com.banking.banking.entity.User;

public interface UserService {

    BankResponseDto createAccount(UserRequestDto userRequest);
   BankResponseDto balanceEnquiry(EnquiryRequestDto enquiryRequestDto);
   BankResponseDto creditAccount(CreditDebitRequest creditDebitRequest) ;
   BankResponseDto debitAccount(CreditDebitRequest creditDebitRequest);
   BankResponseDto transfer(TransferRequest transferRequest);
   BankResponseDto login(LoginDto loginDto);
}
