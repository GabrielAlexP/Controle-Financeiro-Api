package com.financeiro.api.dtos;

import com.financeiro.api.enums.TransactionType;
import com.financeiro.api.models.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponseDTO(
        Long id,
        Long accountId,
        String accountName,
        Long categoryId,
        String categoryName,
        BigDecimal amount,
        TransactionType type,
        String description,
        LocalDate transactionDate
) {
    public TransactionResponseDTO(Transaction t) {
        this(
            t.getId(), 
            t.getAccount().getId(), 
            t.getAccount().getName(),
            t.getCategory().getId(), 
            t.getCategory().getName(),
            t.getAmount(), 
            t.getType(), 
            t.getDescription(), 
            t.getTransactionDate()
        );
    }
}