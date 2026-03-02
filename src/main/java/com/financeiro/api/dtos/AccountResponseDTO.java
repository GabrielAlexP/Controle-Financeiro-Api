package com.financeiro.api.dtos;

import com.financeiro.api.models.Account;
import java.math.BigDecimal;

public record AccountResponseDTO(
        Long id, 
        String name, 
        BigDecimal balance
) {
    public AccountResponseDTO(Account account) {
        this(account.getId(), account.getName(), account.getBalance());
    }
}