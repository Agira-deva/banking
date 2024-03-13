package com.banking.banking.service.implementation;

import com.banking.banking.Utility.AccountUtility;
import com.banking.banking.advice.BalanceNotSufficientException;
import com.banking.banking.advice.EmailSendingException;
import com.banking.banking.advice.InvalidInputException;
import com.banking.banking.configuration.BankingTokenProvider;
import com.banking.banking.dto.*;
import com.banking.banking.entity.Account;
import com.banking.banking.entity.Role;
import com.banking.banking.entity.User;
import com.banking.banking.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    LocalDateTime currentDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    UserRepository userRepository;

    EmailService emailService;
    TransactionService transactionService;

    PasswordEncoder passwordEncoder;

    AuthenticationManager authenticationManager;
    BankingTokenProvider bankingTokenProvider;

    @Autowired
    public UserServiceImp(UserRepository userRepository, TransactionService transactionService
            , EmailService emailService, PasswordEncoder passwordEncoder
            , AuthenticationManager authenticationManager
            , BankingTokenProvider bankingTokenProvider) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.bankingTokenProvider = bankingTokenProvider;
    }


    @Override
    public BankResponseDto createAccount(UserRequestDto userRequest) {
        try {
            if (userRepository.existByEmail(userRequest.getEmail()).isPresent()) {
                return BankResponseDto.builder()
                        .responseCode(AccountUtility.ACCOUNT_EXISTS_CODE)
                        .responseMessage(AccountUtility.ACCOUNT_EXISTS_MESSAGE)
                        .accountInfo(null)
                        .build();

            }
//            if (userRequest.getFirstName() == null || userRequest.getFirstName().isEmpty() ||
//                    userRequest.getLastName() == null || userRequest.getLastName().isEmpty() ||
//                    userRequest.getEmail() == null || userRequest.getEmail().isEmpty() ||
//                    userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
//                throw new InvalidInputException("Invalid user input. Please provide all required information.");
//            }
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
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .phoneNumber(userRequest.getPhoneNumber())
                    .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                    .role(Role.valueOf("ROLE_ADMIN"))
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
        } catch (EmailSendingException exception) {
            return BankResponseDto.builder()
                    .responseCode("ERROR_CODE_EMAIL")
                    .responseMessage("An error occurred while sending the email."+exception.getMessage())
                    .accountInfo(null)
                    .build();
        }catch (Exception exception) {
            return BankResponseDto.builder()
                    .responseCode("UNEXPECTED_ERROR_CODE")
                    .responseMessage("An unexpected error occurred: " + exception.getMessage())
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public BankResponseDto login(LoginDto loginDto) {
        try {
            Authentication authentication = null;
            System.out.println("email" + loginDto.getEmailId());
            System.out.println(loginDto.getPassword());
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmailId(), loginDto.getPassword()));
            System.out.println(authentication);

            EmailDetails loginAlerts = EmailDetails.builder()
                    .subject("You're logged in")
                    .recipient(loginDto.getEmailId())
                    .messageBody(" You logged into your account. If you did not initialize this request, please contact your bank")
                    .build();
            emailService.sendEmailAlerts(loginAlerts);
            return BankResponseDto.builder()
                    .responseCode("Login Success")
                    .responseMessage(bankingTokenProvider.generateToken(authentication))
                    .build();

        } catch (AuthenticationException authenticationException) {
            return BankResponseDto.builder()
                    .responseCode("Login Failed")
                    .responseMessage("Invalid Credentials"+authenticationException.getMessage())
                    .build();
    }catch (Exception exception) {
            return BankResponseDto.builder()
                    .responseCode("UNEXPECTED_ERROR_CODE")
                    .responseMessage("An unexpected error occurred: " + exception.getMessage())
                    .accountInfo(null)
                    .build();
        }
    }


    @Override
    public BankResponseDto balanceEnquiry(EnquiryRequestDto enquiryRequestDto) {
        Optional<User> user = userRepository.existByAccountNumber(enquiryRequestDto.getAccountNumber());
        try {
            if (user.isEmpty()) {
                throw new AccountNotFoundException("Account not found with the provided account number"+enquiryRequestDto.getAccountNumber());
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
        }catch(AccountNotFoundException accountNotFoundException){
            return BankResponseDto.builder()
                    .responseCode(AccountUtility.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtility.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }catch (AuthenticationException exception) {
            return BankResponseDto.builder()
                    .responseCode("AUTHENTICATION_ERROR_CODE")
                    .responseMessage("Authentication failed: " + exception.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception exception) {
            return BankResponseDto.builder()
                    .responseCode("UNEXPECTED_ERROR_CODE")
                    .responseMessage("An unexpected error occurred: " + exception.getMessage())
                    .accountInfo(null)
                    .build();
        }
    }


    @Override
    public BankResponseDto creditAccount(CreditDebitRequest creditDebitRequest) {
        try {
            Optional<User> user = userRepository.existByAccountNumber(creditDebitRequest.getAccountNumber());
            if (user.isEmpty()) {
                return BankResponseDto.builder()
                        .responseCode(AccountUtility.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtility.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }

            User credit = user.get();
            BigDecimal availableBalance = credit.getAccount().getAccountBalance();
            BigDecimal creditedAmount = creditDebitRequest.getAccountBalance();
            credit.getAccount().setAccountBalance(availableBalance.add(creditedAmount));
            userRepository.save(credit);

            TransactionRequest transaction = TransactionRequest.builder()
                    .accountNumber(credit.getAccount().getAccountNumber())
                    .transactionType("CREDIT")
                    .amount(creditDebitRequest.getAccountBalance())
                    .build();
            transactionService.saveTransaction(transaction);

            String accountNumber = credit.getAccount().getAccountNumber();
            String maskedAccountNumber = "*******" + accountNumber.substring(7);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(credit.getEmail())
                    .subject("AMOUNT CREDITED")
                    .messageBody("DEAR CUSTOMER YOUR ACCOUNT " + maskedAccountNumber +
                            " HAS BEEN CREDITED WITH RS.  " +
                            creditedAmount + " And your account balance is Rs. " + credit.getAccount().getAccountBalance() +
                            "\n " + "DATE AND TIME: " + currentDateTime.format(formatter))
                    .build();
            emailService.sendEmailAlerts(emailDetails);
            return BankResponseDto.builder()
                    .responseCode(AccountUtility.ACCOUNT_CREDITED_CODE)
                    .responseMessage(AccountUtility.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(credit.getFirstName() + " " + credit.getLastName() + " " + credit.getOtherName())
                            .accountBalance(credit.getAccount().getAccountBalance())
                            .accountNumber(credit.getAccount().getAccountNumber())
                            .build())
                    .build();
        } catch (EmailSendingException exception) {
            return BankResponseDto.builder()
                    .responseCode("ERROR_CODE")
                    .responseMessage("An error occurred while sending the email."+exception.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception exception) {
            return BankResponseDto.builder()
                    .responseCode("ERROR_CODE")
                    .responseMessage("An error occurred while processing the request."+exception.getMessage())
                    .accountInfo(null)
                    .build();
        }
    }


    @Override
    @SneakyThrows
    public BankResponseDto debitAccount(CreditDebitRequest creditDebitRequest) {
        try {
            Optional<User> user = userRepository.existByAccountNumber(creditDebitRequest.getAccountNumber());
            if (user.isEmpty()) {
                return BankResponseDto.builder()
                        .responseCode(AccountUtility.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtility.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            User debit = user.get();
            BigInteger availableBalance = debit.getAccount().getAccountBalance().toBigInteger();
            BigInteger debitAmount = creditDebitRequest.getAccountBalance().toBigInteger();
            if (availableBalance.intValue() < debitAmount.intValue()) {
                throw new BalanceNotSufficientException("Not Sufficient Balance");
//            return BankResponseDto.builder()
//                    .responseCode(AccountUtility.INSUFFICIENT_CODE)
//                    .responseMessage(AccountUtility.INSUFFICIENT_MESSAGE)
//                    .accountInfo(null)
//                    .build();
            } else {

                BigDecimal currentAccountBalance = debit.getAccount().getAccountBalance();
                BigDecimal amountToBeDebited = creditDebitRequest.getAccountBalance();
                debit.getAccount().setAccountBalance(currentAccountBalance.subtract(amountToBeDebited));
                userRepository.save(debit);
                String accountNumber = debit.getAccount().getAccountNumber();
                String maskedAccountNumber = "*******" + accountNumber.substring(7);

                EmailDetails emailDetails = EmailDetails.builder()
                        .recipient(debit.getEmail())
                        .subject("AMOUNT DEBITED")
                        .messageBody("DEAR CUSTOMER YOUR ACCOUNT " + maskedAccountNumber +
                                " HAS BEEN DEBITED WITH RS.  " + amountToBeDebited +
                                ". Your current account balance is Rs. " + debit.getAccount().getAccountBalance() +
                                "\n " + "DATE AND TIME: " + currentDateTime.format(formatter))
                        .build();
                emailService.sendEmailAlerts(emailDetails);
                TransactionRequest transaction = TransactionRequest.builder()
                        .accountNumber(debit.getAccount().getAccountNumber())
                        .transactionType("DEBIT")
//                   .amount(creditDebitRequest.getAccountBalance())
                        .amount(debit.getAccount().getAccountBalance())
                        .build();
                transactionService.saveTransaction(transaction);
                return BankResponseDto.builder()
                        .responseCode(AccountUtility.ACCOUNT_DEBITED_CODE)
                        .responseMessage(AccountUtility.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountNumber(creditDebitRequest.getAccountNumber())
                                .accountName(debit.getFirstName() + " " + debit.getLastName() + " " + debit.getOtherName())
                                .accountBalance(debit.getAccount().getAccountBalance())

                                .build())
                        .build();

            }
        } catch (EmailSendingException exception) {
            return BankResponseDto.builder()
                    .responseCode("ERROR_CODE")
                    .responseMessage("An error occurred while sending the email."+exception.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception exception) {
            return BankResponseDto.builder()
                    .responseCode("ERROR_CODE")
                    .responseMessage("An error occurred while processing the request."+exception.getMessage())
                    .accountInfo(null)
                    .build();
        }
    }

    @Override
    public BankResponseDto transfer(TransferRequest transferRequest) {
        try {

            Optional<User> destinationAccountExist = userRepository.existByAccountNumber(transferRequest.getDestinationAccountNumber());
            if (destinationAccountExist.isEmpty()) {
                return BankResponseDto.builder()
                        .responseCode(AccountUtility.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtility.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
            Optional<User> sourceAccountUser = userRepository.existByAccountNumber(transferRequest.getSourceAccountNumber());
            if (transferRequest.getAmount().compareTo(sourceAccountUser.get().getAccount().getAccountBalance()) > 0) {
                return BankResponseDto.builder()
                        .responseCode(AccountUtility.INSUFFICIENT_CODE)
                        .responseMessage(AccountUtility.INSUFFICIENT_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
//        sourceAccountUser.get().getAccount().setAccountBalance(sourceAccountUser.get().getAccount().getAccountBalance().subtract(transferRequest.getAmount()));
            User sourceUser = sourceAccountUser.get();
            String sourceUserName = sourceUser.getFirstName() + sourceUser.getLastName() + sourceUser.getOtherName();
            sourceUser.getAccount().setAccountBalance(sourceUser.getAccount().getAccountBalance().subtract(transferRequest.getAmount()));
            userRepository.save(sourceUser);
            EmailDetails debitAlert = EmailDetails.builder()
                    .subject("DEBIT ALERT")
                    .recipient(sourceUser.getEmail())
                    .messageBody("The sum of " + transferRequest.getAmount() + " has been deducted from your account \n" + "Your current balance is Rs. " + sourceUser.getAccount().getAccountBalance())
                    .build();
            emailService.sendEmailAlerts(debitAlert);

            Optional<User> destinationAccountUser = userRepository.existByAccountNumber(transferRequest.getDestinationAccountNumber());
            User destinationUser = destinationAccountExist.get();
            destinationUser.getAccount().setAccountBalance(destinationUser.getAccount().getAccountBalance().add(transferRequest.getAmount()));
            userRepository.save(destinationUser);
//        String destinationUserName= destinationUser.getFirstName()+destinationUser.getLastName()+destinationUser.getOtherName();
            EmailDetails creditAlert = EmailDetails.builder()
                    .subject(" CREDITED")
                    .recipient(destinationUser.getEmail())
                    .messageBody("The sum of " + transferRequest.getAmount() + " has been credited into your account from" + sourceUserName + " \n" + "Your current balance is Rs. " + sourceUser.getAccount().getAccountBalance())
                    .build();
            emailService.sendEmailAlerts(creditAlert);
            TransactionRequest transaction = TransactionRequest.builder()
                    .accountNumber(destinationUser.getAccount().getAccountNumber())
                    .transactionType("Transfer credit")
                    .amount(transferRequest.getAmount())
                    .build();
            transactionService.saveTransaction(transaction);
            return BankResponseDto.builder()
                    .responseCode(AccountUtility.TRANSFER_SUCCESS_CODE)
                    .responseMessage(AccountUtility.TRANSFER_SUCCESS_MESSAGE)
                    .accountInfo(null)
                    .build();
        } catch (TransactionException exception) {
            return BankResponseDto.builder()
                    .responseCode("ERROR_CODE")
                    .responseMessage("An error occurred while processing the transaction."+exception.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (EmailSendingException emailSendingException) {
            return BankResponseDto.builder()
                    .responseCode("ERROR_CODE")
                    .responseMessage("An error occurred while sending the email."+emailSendingException.getMessage())
                    .accountInfo(null)
                    .build();
        } catch (Exception exception) {
            return BankResponseDto.builder()
                    .responseCode("ERROR_CODE")
                    .responseMessage("An error occurred while processing the request!!! Please check the value entered in the fields are valid."+exception.getMessage())
                    .accountInfo(null)
                    .build();
        }
    }


}
