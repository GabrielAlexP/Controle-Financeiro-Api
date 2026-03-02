package com.financeiro.api.jobs;

import com.financeiro.api.models.Goal;
import com.financeiro.api.repositories.GoalRepository;
import com.financeiro.api.services.BacenIndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class GoalYieldJob {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private BacenIndicatorService bacenService;

    @Scheduled(cron = "0 0 2 * * *")
    public void applyDailyYields() {

        BigDecimal dailyCdiPercentage = bacenService.getIndicators().get("CDI_DIARIA");
        
        if (dailyCdiPercentage == null) {
            System.err.println("Taxa CDI Diária não encontrada no cache. Abortando rendimentos hoje.");
            return;
        }

        BigDecimal multiplier = dailyCdiPercentage.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);

        List<Goal> activeGoals = goalRepository.findByYieldsCdiTrue();

        int updatedCount = 0;

        for (Goal goal : activeGoals) {

            if (goal.getCurrentAmount() != null && goal.getCurrentAmount().compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal yieldAmount = goal.getCurrentAmount().multiply(multiplier);

                BigDecimal newBalance = goal.getCurrentAmount().add(yieldAmount);

                goal.setCurrentAmount(newBalance.setScale(2, RoundingMode.HALF_UP));
                updatedCount++;
            }
        }

        goalRepository.saveAll(activeGoals);
    }
}