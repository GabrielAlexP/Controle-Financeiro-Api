package com.financeiro.api.repositories;

import com.financeiro.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByUsername(String username);
    
    void deleteAllByIsGuestTrueAndCreatedAtBefore(LocalDateTime dateTime);
}