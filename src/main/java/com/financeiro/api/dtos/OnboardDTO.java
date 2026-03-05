package com.financeiro.api.dtos;

import java.math.BigDecimal;

public record OnboardDTO(
    String accountName,
    BigDecimal initialBalance
) {}