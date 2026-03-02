package com.financeiro.api.dtos;

import com.financeiro.api.models.Goal;
import java.math.BigDecimal;

public record GoalResponseDTO(
        Long id, String name, BigDecimal targetAmount, 
        BigDecimal currentAmount, Boolean yieldsCdi
) {
    public GoalResponseDTO(Goal g) {
        this(g.getId(), g.getName(), g.getTargetAmount(), g.getCurrentAmount(), g.getYieldsCdi());
    }
}