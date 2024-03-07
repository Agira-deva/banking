package com.banking.banking.service.implementation;

import com.banking.banking.dto.BankResponseDto;
import com.banking.banking.dto.CreditDebitRequest;
import com.banking.banking.dto.EnquiryRequestDto;
import com.banking.banking.dto.UserRequestDto;

public interface UserService {

   BankResponseDto createAccount(UserRequestDto userRequest);
   BankResponseDto balanceEnquiry(EnquiryRequestDto enquiryRequestDto);
   BankResponseDto creditAccount(CreditDebitRequest creditDebitRequest);
   BankResponseDto debitAccount(CreditDebitRequest creditDebitRequest);
}
