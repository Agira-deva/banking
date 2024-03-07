package com.banking.banking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private  String firstName;
    private  String lastName;
    private  String otherName;
    private String gender;
    private  String address;
    private String stateOfOrigin;
//    private String accountNumber;
//    private BigDecimal accountBalance;
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
//    @ManyToMany
//    private List<Recipient> recipients;
//    @OneToMany(mappedBy = "user")
//    private List<Transaction> transactions;
    private String email;
    private String phoneNumber;
    private String alternativePhoneNumber;
//    private String status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime modifiedAt;

}
