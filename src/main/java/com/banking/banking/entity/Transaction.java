package com.banking.banking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
//    private Long id;
//    private Double amount;
//    private String transactionId;
    private String transactionId;
    private BigDecimal amount;
    private String transactionType;
    private String accountNumber;
    private  String status;
//    @ManyToOne
//    private User user;
//    @ManyToOne
//    private Recipient recipient;
//    private LocalDateTime dateTime;

}
