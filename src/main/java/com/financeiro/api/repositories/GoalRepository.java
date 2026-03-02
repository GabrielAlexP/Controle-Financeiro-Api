package com.financeiro.api.repositories;

import com.financeiro.api.models.Goal;
import com.financeiro.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUser(User user);
    Optional<Goal> findByIdAndUser(Long id, User user);
    
    List<Goal> findByYieldsCdiTrue(); 
}