package com.financeiro.api.jobs;

import com.financeiro.api.enums.YieldType;
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

        BigDecimal cdiDaily = bacenService.getIndicators().get("CDI_DIARIA");
        BigDecimal selicDaily = bacenService.getIndicators().get("SELIC_DIARIA");
        
        if (cdiDaily == null || selicDaily == null) {
            System.err.println("Taxas não encontradas no cache. Abortando rendimentos hoje.");
            return;
        }

        BigDecimal cdiMultiplier = cdiDaily.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);
        BigDecimal selicMultiplier = selicDaily.divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP);

        List<Goal> activeGoals = goalRepository.findByYieldTypeNot(YieldType.NONE);

        for (Goal goal : activeGoals) {
            if (goal.getCurrentAmount() != null && goal.getCurrentAmount().compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal multiplier = goal.getYieldType() == YieldType.CDI ? cdiMultiplier : selicMultiplier;
                BigDecimal yieldAmountForToday = goal.getCurrentAmount().multiply(multiplier);

                goal.setCurrentAmount(goal.getCurrentAmount().add(yieldAmountForToday).setScale(2, RoundingMode.HALF_UP));
                
                BigDecimal currentYield = goal.getYieldAmount() != null ? goal.getYieldAmount() : BigDecimal.ZERO;
                goal.setYieldAmount(currentYield.add(yieldAmountForToday).setScale(2, RoundingMode.HALF_UP));
            }
        }

        goalRepository.saveAll(activeGoals);
    }
}