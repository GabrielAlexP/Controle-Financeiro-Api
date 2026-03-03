package com.financeiro.api.dtos;

import com.financeiro.api.enums.YieldType;
import com.financeiro.api.models.Goal;
import java.math.BigDecimal;

public record GoalResponseDTO(
        Long id, String name, String institution, BigDecimal targetAmount, 
        BigDecimal currentAmount, BigDecimal yieldAmount, YieldType yieldType
) {
    public GoalResponseDTO(Goal g) {
        this(g.getId(), g.getName(), g.getInstitution(), g.getTargetAmount(), 
             g.getCurrentAmount(), g.getYieldAmount(), g.getYieldType());
    }
}