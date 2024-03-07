package com.banking.banking.service.implementation;

import com.banking.banking.dto.TransactionRequest;
import com.banking.banking.entity.Transaction;
import com.banking.banking.repository.TransactionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Component
public class TransactionImp implements TransactionService{
   @Autowired
    TransactionalRepository transactionalRepository;

    @Override
    public void saveTransaction(TransactionRequest transaction) {
        Transaction transaction1=Transaction.builder()
                .transactionType(transaction.getTransactionType())
                .accountNumber(transaction.getAccountNumber())
                .amount(transaction.getAmount())
                .status("SUCCESS")
                .build();
        transactionalRepository.save(transaction1);
    }
}
