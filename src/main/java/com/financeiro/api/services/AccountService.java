package com.financeiro.api.services;

import com.financeiro.api.dtos.AccountRequestDTO;
import com.financeiro.api.dtos.AccountResponseDTO;
import com.financeiro.api.models.Account;
import com.financeiro.api.models.User;
import com.financeiro.api.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public AccountResponseDTO create(AccountRequestDTO data) {
        Account account = Account.builder()
                .user(getAuthenticatedUser())
                .name(data.name())
                .balance(data.balance() != null ? data.balance() : BigDecimal.ZERO) 
                .build();
                
        return new AccountResponseDTO(accountRepository.save(account));
    }

    public List<AccountResponseDTO> listAll() {
        return accountRepository.findByUser(getAuthenticatedUser())
                .stream()
                .map(AccountResponseDTO::new)
                .toList();
    }

    public AccountResponseDTO update(Long id, AccountRequestDTO data) {
        Account account = accountRepository.findByIdAndUser(id, getAuthenticatedUser())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada."));

        account.setName(data.name());
        if (data.balance() != null) {
            account.setBalance(data.balance());
        }

        return new AccountResponseDTO(accountRepository.save(account));
    }

    public void delete(Long id) {
        Account account = accountRepository.findByIdAndUser(id, getAuthenticatedUser())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada."));
        accountRepository.delete(account);
    }
}