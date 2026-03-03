package com.financeiro.api.services;

import com.financeiro.api.dtos.GoalRequestDTO;
import com.financeiro.api.dtos.GoalResponseDTO;
import com.financeiro.api.dtos.GoalUpdateAmountDTO;
import com.financeiro.api.models.Goal;
import com.financeiro.api.models.User;
import com.financeiro.api.repositories.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GoalService {

    @Autowired private GoalRepository goalRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public GoalResponseDTO create(GoalRequestDTO data) {
        Goal goal = Goal.builder()
                .user(getAuthenticatedUser())
                .name(data.name())
                .institution(data.institution())
                .targetAmount(data.targetAmount())
                .currentAmount(BigDecimal.ZERO)
                .yieldAmount(BigDecimal.ZERO)
                .yieldType(data.yieldType())
                .build();
        return new GoalResponseDTO(goalRepository.save(goal));
    }

    public List<GoalResponseDTO> listAll() {
        return goalRepository.findByUser(getAuthenticatedUser())
                .stream().map(GoalResponseDTO::new).toList();
    }

    public GoalResponseDTO updateAmount(Long id, GoalUpdateAmountDTO data) {
        Goal goal = goalRepository.findByIdAndUser(id, getAuthenticatedUser())
                .orElseThrow(() -> new RuntimeException("Caixinha não encontrada."));

        goal.setCurrentAmount(data.currentAmount());
        return new GoalResponseDTO(goalRepository.save(goal));
    }

    public void delete(Long id) {
        Goal goal = goalRepository.findByIdAndUser(id, getAuthenticatedUser())
                .orElseThrow(() -> new RuntimeException("Caixinha não encontrada."));
        goalRepository.delete(goal);
    }
}