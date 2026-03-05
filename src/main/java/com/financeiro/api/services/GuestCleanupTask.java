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
    // Hack para testes: "0 * * * * *" significa rodar no segundo 0 de TODO MINUTO
    @Scheduled(cron = "0 * * * * *")
    public void cleanupGuestUsers() {
        // Hack para testes: Considera "antigo" qualquer um criado há mais de 1 minuto (em vez de 2 horas)
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(1);
        userRepository.deleteAllByIsGuestTrueAndCreatedAtBefore(cutoff);
        
        // Um print apenas para você ver o faxineiro passando no terminal do Spring!
        System.out.println("🧹 [FAXINEIRO] Varredura executada às " + LocalDateTime.now());
    }
}