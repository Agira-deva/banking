package com.banking.banking.service.implementation;

import com.banking.banking.dto.TransactionRequest;

public interface TransactionService {
    void saveTransaction(TransactionRequest transaction);
}
