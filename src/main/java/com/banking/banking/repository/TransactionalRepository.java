package com.banking.banking.repository;

import com.banking.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionalRepository extends JpaRepository<Transaction,String> {
}
