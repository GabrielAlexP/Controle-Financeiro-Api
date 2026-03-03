package com.financeiro.api.services;

import com.financeiro.api.dtos.CreditCardRequestDTO;
import com.financeiro.api.dtos.CreditCardResponseDTO;
import com.financeiro.api.models.Account;
import com.financeiro.api.models.CreditCard;
import com.financeiro.api.models.User;
import com.financeiro.api.repositories.AccountRepository;
import com.financeiro.api.repositories.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private AccountRepository accountRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public CreditCardResponseDTO create(CreditCardRequestDTO data) {
        User user = getAuthenticatedUser();

        Account account = accountRepository.findByIdAndUser(data.accountId(), user)
                .orElseThrow(() -> new RuntimeException("Conta bancária não encontrada."));

        CreditCard card = CreditCard.builder()
                .account(account)
                .name(data.name())
                .limitAmount(data.limitAmount())
                .closingDay(data.closingDay())
                .dueDay(data.dueDay())
                .color1Hex(data.color1Hex())
                .color2Hex(data.color2Hex())
                .build();

        return new CreditCardResponseDTO(creditCardRepository.save(card));
    }

    public List<CreditCardResponseDTO> listAll() {
        return creditCardRepository.findAllByUser(getAuthenticatedUser())
                .stream()
                .map(CreditCardResponseDTO::new)
                .toList();
    }

    public CreditCardResponseDTO update(Long id, CreditCardRequestDTO data) {
        User user = getAuthenticatedUser();

        CreditCard card = creditCardRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Cartão de crédito não encontrado."));

        Account account = accountRepository.findByIdAndUser(data.accountId(), user)
                .orElseThrow(() -> new RuntimeException("Conta bancária não encontrada."));

        card.setAccount(account);
        card.setName(data.name());
        card.setLimitAmount(data.limitAmount());
        card.setClosingDay(data.closingDay());
        card.setDueDay(data.dueDay());
        card.setColor1Hex(data.color1Hex());
        card.setColor2Hex(data.color2Hex());

        return new CreditCardResponseDTO(creditCardRepository.save(card));
    }

    public void delete(Long id) {
        CreditCard card = creditCardRepository.findByIdAndUser(id, getAuthenticatedUser())
                .orElseThrow(() -> new RuntimeException("Cartão de crédito não encontrado."));
        creditCardRepository.delete(card);
    }
}