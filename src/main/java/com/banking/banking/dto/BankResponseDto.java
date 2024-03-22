package com.banking.banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankResponseDto {

    private String responseCode;
    private String responseMessage;
    private AccountInfo  accountInfo;
}
