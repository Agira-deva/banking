package com.banking.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class TransactionRequest {
    private BigDecimal amount;
    private String transactionType;
    private String accountNumber;
    private  String status;
}
