package com.financeiro.api.dtos;

import com.financeiro.api.models.CreditCard;
import java.math.BigDecimal;

public record CreditCardResponseDTO(
        Long id, Long accountId, String accountName,
        String name, BigDecimal limitAmount, 
        Integer closingDay, Integer dueDay,
        String color1Hex, String color2Hex
) {
    public CreditCardResponseDTO(CreditCard card) {
        this(card.getId(), card.getAccount().getId(), card.getAccount().getName(),
             card.getName(), card.getLimitAmount(), card.getClosingDay(), card.getDueDay(),
             card.getColor1Hex(), card.getColor2Hex());
    }
}