package com.banking.banking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class  Transaction  {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    private BigDecimal amount;
    private String transactionType;
    private String accountNumber;
    private  String status;
    @ManyToOne
    private User user;
    @CreationTimestamp
    private LocalDate createdAt;

}
