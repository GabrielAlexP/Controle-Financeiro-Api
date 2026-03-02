package com.financeiro.api.dtos;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record AccountRequestDTO(
        @NotBlank(message = "O nome da conta é obrigatório") 
        String name,
        BigDecimal balance
) {}