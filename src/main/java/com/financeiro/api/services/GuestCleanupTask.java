package com.financeiro.api.services;

import com.financeiro.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class GuestCleanupTask {

    @Autowired
    private UserRepository userRepository;

    @Transactional

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupGuestUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(2);
        userRepository.deleteAllByIsGuestTrueAndCreatedAtBefore(cutoff);
    }
}