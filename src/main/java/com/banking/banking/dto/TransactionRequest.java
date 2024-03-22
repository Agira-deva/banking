package com.banking.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class TransactionRequest {
    private BigDecimal amount;
    private String transactionType;
    private String fromAccount;
    private String toAccount;
    private LocalDate createdAt;
    private LocalDate modifiedAt;
    private  String status;
}
