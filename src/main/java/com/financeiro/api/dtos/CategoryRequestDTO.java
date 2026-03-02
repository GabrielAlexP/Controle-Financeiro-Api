package com.financeiro.api.dtos;

import com.financeiro.api.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequestDTO(
        @NotBlank(message = "O nome da categoria é obrigatório") 
        String name,
        @NotNull(message = "O tipo da transação é obrigatório") 
        TransactionType type,
        String colorHex
) {}