package com.financeiro.api.dtos;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record GoalUpdateAmountDTO(
        @NotNull(message = "O valor atual é obrigatório")
        BigDecimal currentAmount
) {}