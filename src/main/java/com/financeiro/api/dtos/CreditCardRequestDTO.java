package com.financeiro.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreditCardRequestDTO(
        @NotNull(message = "ID da conta vinculada é obrigatório") Long accountId,
        @NotBlank(message = "O nome do cartão é obrigatório") String name,
        @NotNull(message = "O limite do cartão é obrigatório")
        @Positive(message = "O limite deve ser maior que zero") BigDecimal limitAmount,
        @NotNull(message = "O dia de fechamento é obrigatório") Integer closingDay,
        @NotNull(message = "O dia de vencimento é obrigatório") Integer dueDay
) {}