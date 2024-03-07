package com.banking.banking.service.implementation;

import com.banking.banking.Utility.AccountUtility;
import com.banking.banking.dto.*;
import com.banking.banking.entity.Account;
import com.banking.banking.entity.User;
import com.banking.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;

    @Override
    public BankResponseDto createAccount(UserRequestDto userRequest) {
//        String something = RandomStringUtils.randomAlphanumeric(10);
//        System.out.println(something);
        if (userRepository.existByEmail(userRequest.getEmail()).isPresent()) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtility.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtility.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
        User newUser = User.builder().firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .account(Account.builder().accountNumber(AccountUtility.generateAccountNumber())
                        .accountBalance(BigDecimal.ZERO)
                        .status("ACTIVE")
                        .build())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .build();
        User savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("CONGRATULATIONS YOUR ACCOUNT HAS BEEN SUCCESSFULLY CREATED. \n YOUR ACCOUNT DETAILS\n" +
                        "ACCOUNT NAME :" + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + " \nACCOUNT NUMBER: " + savedUser.getAccount().getAccountNumber() + "\n" + "DATE AND TIME: " + currentDateTime.format(formatter))
                .build();
        emailService.sendEmailAlerts(emailDetails);

        return BankResponseDto.builder()
                .responseCode(AccountUtility.ACCOUNT_CREATED_SUCCESS)
                .responseMessage(AccountUtility.ACCOUNT_CREATED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccount().getAccountBalance())
                        .accountNumber(savedUser.getAccount().getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponseDto balanceEnquiry(EnquiryRequestDto enquiryRequestDto) {
        Optional<User> user = userRepository.existByAccountNumber(enquiryRequestDto.getAccountNumber());
        if (user.isEmpty()) {
            return BankResponseDto.builder()
                    .responseCode(AccountUtility.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtility.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
//        User foundUser = userRepository.findByAccountNumber(enquiryRequestDto.getAccountNumber());
        User foundUser = user.get();
        return BankResponseDto.builder()
                .responseCode(AccountUtility.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtility.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccount().getAccountBalance())
                        .accountNumber(foundUser.getAccount().getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }



    @Override
    public BankResponseDto creditAccount(CreditDebitRequest creditDebitRequest) {
        Optional<User> user = userRepository.existByAccountNumber(creditDebitRequest.getAccountNumber());
        if (user.isEmpty()){
            return BankResponseDto.builder()
                    .responseCode(AccountUtility.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtility.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User credit=user.get();
        BigDecimal availableBalance = credit.getAccount().getAccountBalance();
        BigDecimal creditedAmount = creditDebitRequest.getAccountBalance();
        credit.getAccount().setAccountBalance(availableBalance.add(creditedAmount));
        userRepository.save(credit);

        String accountNumber = credit.getAccount().getAccountNumber();
        String maskedAccountNumber = "*******" + accountNumber.substring(7);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(credit.getEmail())
                .subject("AMOUNT CREDITED")
                .messageBody("DEAR CUSTOMER YOUR ACCOUNT "+ maskedAccountNumber +
                        " HAS BEEN CREDITED WITH RS.  " +
                       creditedAmount+" And your acccount balance is Rs. "+availableBalance+
                        "\n "+ "DATE AND TIME: " + currentDateTime.format(formatter))
                .build();
        emailService.sendEmailAlerts(emailDetails);
        return BankResponseDto.builder()
                .responseCode(AccountUtility.ACCOUNT_CREDITED_CODE)
                .responseMessage(AccountUtility.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(credit.getFirstName()+" "+credit.getLastName()+" "+ credit.getOtherName())
                        .accountBalance(availableBalance)
                        .accountNumber(credit.getAccount().getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponseDto debitAccount(CreditDebitRequest creditDebitRequest) {
        Optional<User> user = userRepository.existByAccountNumber(creditDebitRequest.getAccountNumber());
        if (user.isEmpty()){
            return BankResponseDto.builder()
                    .responseCode(AccountUtility.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtility.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
    }
        User debit=user.get();
        BigInteger availableBalance=debit.getAccount().getAccountBalance().toBigInteger();
        BigInteger debitAmount=creditDebitRequest.getAccountBalance().toBigInteger();
       if (availableBalance.intValue()< debitAmount.intValue()){
           return  BankResponseDto.builder()
                   .responseCode(AccountUtility.INSUFFICIENT_CODE)
                   .responseMessage(AccountUtility.INSUFFICIENT_MESSAGE)
                   .accountInfo(null)
                   .build();
       }else {

           BigDecimal currentAccountBalance = debit.getAccount().getAccountBalance();
           BigDecimal amountToBeDebited = creditDebitRequest.getAccountBalance();
           debit.getAccount().setAccountBalance(currentAccountBalance.subtract(amountToBeDebited));
           userRepository.save(debit);
           String accountNumber = debit.getAccount().getAccountNumber();
           String maskedAccountNumber = "*******" + accountNumber.substring(7);

           EmailDetails emailDetails = EmailDetails.builder()
                   .recipient(debit.getEmail())
                   .subject("AMOUNT DEBITED")
                   .messageBody("DEAR CUSTOMER YOUR ACCOUNT "+ maskedAccountNumber +
                                   " HAS BEEN DEBITED WITH RS.  " +amountToBeDebited+
                                   ". Your current account balance is Rs. " + debit.getAccount().getAccountBalance() +
                            "\n "+ "DATE AND TIME: " + currentDateTime.format(formatter))
                   .build();
           emailService.sendEmailAlerts(emailDetails);
           return BankResponseDto.builder()
                   .responseCode(AccountUtility.ACCOUNT_DEBITED_CODE)
                   .responseMessage(AccountUtility.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                   .accountInfo(AccountInfo.builder()
                           .accountNumber(creditDebitRequest.getAccountNumber())
                           .accountName(debit.getFirstName()+" "+debit.getLastName()+" "+debit.getOtherName())
                           .accountBalance(debit.getAccount().getAccountBalance())

                           .build())
                   .build();

       }
       }


}
