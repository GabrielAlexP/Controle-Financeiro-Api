package com.financeiro.api.services;

import com.financeiro.api.dtos.TransactionRequestDTO;
import com.financeiro.api.dtos.TransactionResponseDTO;
import com.financeiro.api.enums.TransactionType;
import com.financeiro.api.models.*;
import com.financeiro.api.repositories.AccountRepository;
import com.financeiro.api.repositories.CategoryRepository;
import com.financeiro.api.repositories.CreditCardRepository;
import com.financeiro.api.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CreditCardRepository creditCardRepository;

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Transactional 
    public TransactionResponseDTO create(TransactionRequestDTO data) {
        User user = getAuthenticatedUser();

        Account account = accountRepository.findByIdAndUser(data.accountId(), user)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada."));

        Category category = categoryRepository.findByIdAndUserAndIsActiveTrue(data.categoryId(), user)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada ou inativa."));

        CreditCard creditCard = null;
        if (data.creditCardId() != null) {
            creditCard = creditCardRepository.findByIdAndUser(data.creditCardId(), user)
                    .orElseThrow(() -> new RuntimeException("Cartão de crédito não encontrado."));
            
            if (!creditCard.getAccount().getId().equals(account.getId())) {
                throw new RuntimeException("Este cartão não pertence à conta selecionada.");
            }
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .account(account)
                .creditCard(creditCard)
                .category(category)
                .amount(data.amount())
                .type(data.type())
                .description(data.description())
                .transactionDate(data.transactionDate())
                .build();

        if (creditCard == null) {
            if (data.type() == TransactionType.INCOME) {
                account.setBalance(account.getBalance().add(data.amount()));
            } else {
                account.setBalance(account.getBalance().subtract(data.amount()));
            }
            accountRepository.save(account);
        }

        return new TransactionResponseDTO(transactionRepository.save(transaction));
    }

    public List<TransactionResponseDTO> listAll() {
        return transactionRepository.findByUserOrderByTransactionDateDesc(getAuthenticatedUser())
                .stream().map(TransactionResponseDTO::new).toList();
    }

    @Transactional
    public void delete(Long id) {
        User user = getAuthenticatedUser();
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada."));

        Account account = transaction.getAccount();

        if (transaction.getCreditCard() == null) {
            if (transaction.getType() == TransactionType.INCOME) {
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            } else {
                account.setBalance(account.getBalance().add(transaction.getAmount()));
            }
            accountRepository.save(account);
        }

        transactionRepository.delete(transaction);
    }
}