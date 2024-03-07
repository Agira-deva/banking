package com.banking.banking.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditDebitRequest {
    private String accountNumber;
    private BigDecimal accountBalance;
}
