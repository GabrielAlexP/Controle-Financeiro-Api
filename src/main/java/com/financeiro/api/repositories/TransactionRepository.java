package com.financeiro.api.repositories;

import com.financeiro.api.models.Transaction;
import com.financeiro.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserOrderByTransactionDateDesc(User user);
    
    Optional<Transaction> findByIdAndUser(Long id, User user);
}