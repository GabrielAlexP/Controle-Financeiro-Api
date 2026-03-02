package com.financeiro.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record GoalRequestDTO(
        @NotBlank(message = "O nome da caixinha é obrigatório") 
        String name,
        
        BigDecimal targetAmount,
        
        @NotNull(message = "Defina se a caixinha rende CDI") 
        Boolean yieldsCdi
) {}