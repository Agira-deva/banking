package com.banking.banking.controller;

import com.banking.banking.dto.*;
import com.banking.banking.service.implementation.UserService;
import com.banking.banking.service.implementation.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public BankResponseDto createAccount(@RequestBody UserRequestDto user) {
        return userService.createAccount(user);
    }

    @PostMapping("login")
    public BankResponseDto login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("balanceEnquiry")
    public BankResponseDto balanceEnquiry(@RequestBody EnquiryRequestDto enquiry) {
        return userService.balanceEnquiry(enquiry);
    }

    @PostMapping("credit")
    public BankResponseDto creditAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
        return userService.creditAccount(creditDebitRequest);
    }

    @PostMapping("debit")
    public BankResponseDto debitAccount(@RequestBody CreditDebitRequest creditDebitRequest) {
        return userService.debitAccount(creditDebitRequest);
    }

    @PostMapping("transfer")
    public BankResponseDto transfer(@RequestBody TransferRequest transferRequest) {
        return userService.transfer(transferRequest);
    }
}

