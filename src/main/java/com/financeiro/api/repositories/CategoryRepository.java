package com.financeiro.api.repositories;

import com.financeiro.api.models.Category;
import com.financeiro.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserAndIsActiveTrue(User user);
    
    Optional<Category> findByIdAndUserAndIsActiveTrue(Long id, User user);
}