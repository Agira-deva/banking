package com.banking.banking.repository;

import com.banking.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionalRepository extends JpaRepository<Transaction,String> {
    Optional<List<Transaction>> findByFromAccountAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(String accountNumber, LocalDate startDate, LocalDate endDate);
}
