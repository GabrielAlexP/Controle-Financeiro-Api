package com.financeiro.api.dtos;

import com.financeiro.api.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequestDTO(
        @NotNull(message = "A conta é obrigatória") Long accountId,

        Long creditCardId, 
        
        @NotNull(message = "A categoria é obrigatória") Long categoryId,
        
        @NotNull(message = "O valor é obrigatório") 
        @Positive(message = "O valor deve ser maior que zero") BigDecimal amount,
        
        @NotNull(message = "O tipo é obrigatório") TransactionType type,
        
        String description,
        
        @NotNull(message = "A data da transação é obrigatória") LocalDate transactionDate,
        
        Boolean isPaid,
        Boolean isFixed,
        Integer installments
) {}