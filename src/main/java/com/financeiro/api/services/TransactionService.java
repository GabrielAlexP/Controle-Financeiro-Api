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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
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
    public List<TransactionResponseDTO> create(TransactionRequestDTO data) {
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

        int totalInstallments = (data.installments() != null && data.installments() > 0) ? data.installments() : 1;
        boolean isFixed = data.isFixed() != null ? data.isFixed() : false;
        boolean firstIsPaid = data.isPaid() != null ? data.isPaid() : true;

        BigDecimal totalAmount = data.amount();
        BigDecimal installmentAmount = totalAmount.divide(new BigDecimal(totalInstallments), 2, RoundingMode.HALF_UP);
        BigDecimal remainder = totalAmount.subtract(installmentAmount.multiply(new BigDecimal(totalInstallments)));

        List<Transaction> generatedTransactions = new ArrayList<>();

        for (int i = 1; i <= totalInstallments; i++) {
            BigDecimal currentAmount = (i == 1) ? installmentAmount.add(remainder) : installmentAmount;
            
            boolean currentIsPaid = (i == 1) ? firstIsPaid : false; 
            LocalDate currentDate = data.transactionDate().plusMonths(i - 1);

            Transaction transaction = Transaction.builder()
                    .user(user)
                    .account(account)
                    .creditCard(creditCard)
                    .category(category)
                    .amount(currentAmount)
                    .type(data.type())
                    .description(data.description())
                    .transactionDate(currentDate)
                    .isPaid(currentIsPaid)
                    .isFixed(isFixed)
                    .installmentCurrent(totalInstallments > 1 ? i : null)
                    .installmentTotal(totalInstallments > 1 ? totalInstallments : null)
                    .build();

            generatedTransactions.add(transaction);

            if (creditCard == null && currentIsPaid) {
                if (data.type() == TransactionType.INCOME) {
                    account.setBalance(account.getBalance().add(currentAmount));
                } else {
                    account.setBalance(account.getBalance().subtract(currentAmount));
                }
            }
        }

        if (creditCard == null) {
            accountRepository.save(account);
        }

        List<Transaction> savedTransactions = transactionRepository.saveAll(generatedTransactions);
        
        return savedTransactions.stream().map(TransactionResponseDTO::new).toList();
    }

    public List<TransactionResponseDTO> listAll() {
        return transactionRepository.findByUserOrderByTransactionDateDesc(getAuthenticatedUser())
                .stream().map(TransactionResponseDTO::new).toList();
    }

    @Transactional
    public TransactionResponseDTO markAsPaid(Long id) {
        User user = getAuthenticatedUser();
        Transaction tx = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada."));

        if (tx.getIsPaid()) {
            throw new RuntimeException("Transação já está paga.");
        }

        tx.setIsPaid(true);

        if (tx.getCreditCard() == null) {
            Account account = tx.getAccount();
            if (tx.getType() == TransactionType.INCOME) {
                account.setBalance(account.getBalance().add(tx.getAmount()));
            } else {
                account.setBalance(account.getBalance().subtract(tx.getAmount()));
            }
            accountRepository.save(account);
        }

        return new TransactionResponseDTO(transactionRepository.save(tx));
    }

    @Transactional
    public void payCreditCardBill(Long cardId, String yearMonth) {
        User user = getAuthenticatedUser();
        CreditCard card = creditCardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado."));

        Account account = card.getAccount();

        List<Transaction> allTx = transactionRepository.findByUserOrderByTransactionDateDesc(user);
        
        List<Transaction> billTx = allTx.stream()
                .filter(tx -> tx.getCreditCard() != null && tx.getCreditCard().getId().equals(cardId))
                .filter(tx -> !tx.getIsPaid())
                .filter(tx -> {
                    if (tx.getTransactionDate() == null) return false;
                    String txMonth = tx.getTransactionDate().toString().substring(0, 7);
                    return txMonth.equals(yearMonth);
                })
                .toList();

        if (billTx.isEmpty()) {
            throw new RuntimeException("Nenhuma transação pendente para esta fatura.");
        }

        BigDecimal totalBill = BigDecimal.ZERO;
        for (Transaction tx : billTx) {
            totalBill = totalBill.add(tx.getAmount());
            tx.setIsPaid(true);
        }

        account.setBalance(account.getBalance().subtract(totalBill));
        accountRepository.save(account);
        transactionRepository.saveAll(billTx);
    }

    @Transactional
    public void delete(Long id) {
        User user = getAuthenticatedUser();
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada."));

        Account account = transaction.getAccount();

        if (transaction.getCreditCard() == null && Boolean.TRUE.equals(transaction.getIsPaid())) {
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