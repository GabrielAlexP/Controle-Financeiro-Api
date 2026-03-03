package com.financeiro.api.dtos;

import com.financeiro.api.enums.YieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record GoalRequestDTO(
        @NotBlank(message = "O nome é obrigatório") String name,
        String institution,
        BigDecimal targetAmount,
        @NotNull(message = "Defina o tipo de rendimento") YieldType yieldType
) {}