package com.banking.banking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Recipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String AccountNumber;
    @ManyToMany(mappedBy = "recipients")
    private List<User> user;
    @OneToMany(mappedBy = "recipient")
    private List<Transaction> transactions;
}
