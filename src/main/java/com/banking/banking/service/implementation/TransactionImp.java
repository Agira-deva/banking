package com.banking.banking.service.implementation;

import com.banking.banking.dto.TransactionRequest;
import com.banking.banking.entity.Transaction;
import com.banking.banking.repository.TransactionalRepository;
import jakarta.transaction.InvalidTransactionException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Component
public class TransactionImp implements TransactionService{
   @Autowired
    TransactionalRepository transactionalRepository;
    @SneakyThrows
    @Override
    public void saveTransaction(TransactionRequest transaction) {
        try {
            Transaction transaction1 = Transaction.builder()
                    .transactionType(transaction.getTransactionType())
                    .accountNumber(transaction.getAccountNumber())
                    .amount(transaction.getAmount())
                    .createdAt(transaction.getCreatedAt())
                    .status("SUCCESS")
                    .build();
            transactionalRepository.save(transaction1);
        }catch (DataIntegrityViolationException dataIntegrityViolationException){
            throw new InvalidTransactionException("Failed to save transaction. Invalid transaction data."+dataIntegrityViolationException);
        }
    }
}
