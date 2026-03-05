package com.financeiro.api.services;

import com.financeiro.api.enums.TransactionType;
import com.financeiro.api.enums.YieldType;
import com.financeiro.api.models.*;
import com.financeiro.api.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DataSeederService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public void seedGuestData(User user) {
        Category catSalary = categoryRepository.save(Category.builder().user(user).name("Salário").type(TransactionType.INCOME).colorHex("#10B981").icon("💰").build());
        Category catHousing = categoryRepository.save(Category.builder().user(user).name("Moradia").type(TransactionType.EXPENSE).colorHex("#EF4444").icon("🏠").build());
        Category catFood = categoryRepository.save(Category.builder().user(user).name("Alimentação").type(TransactionType.EXPENSE).colorHex("#F59E0B").icon("🍔").build());
        Category catTransport = categoryRepository.save(Category.builder().user(user).name("Transporte").type(TransactionType.EXPENSE).colorHex("#3B82F6").icon("🚗").build());
        Category catLeisure = categoryRepository.save(Category.builder().user(user).name("Lazer").type(TransactionType.EXPENSE).colorHex("#8B5CF6").icon("🎉").build());
        Category catHealth = categoryRepository.save(Category.builder().user(user).name("Saúde").type(TransactionType.EXPENSE).colorHex("#EC4899").icon("⚕️").build());
        Category catInvest = categoryRepository.save(Category.builder().user(user).name("Investimentos").type(TransactionType.EXPENSE).colorHex("#14B8A6").icon("📈").build());

        Account itau = accountRepository.save(Account.builder().user(user).name("Itaú").balance(BigDecimal.valueOf(3450.00)).build());
        Account nubank = accountRepository.save(Account.builder().user(user).name("Nubank").balance(BigDecimal.valueOf(1240.50)).build());

        CreditCard ccNubank = creditCardRepository.save(CreditCard.builder()
                .account(nubank).name("Cartão Nubank").limitAmount(BigDecimal.valueOf(4500.00))
                .closingDay(25).dueDay(5).color1Hex("#8B5CF6").color2Hex("#C084FC").build());

        Goal goalEmergency = Goal.builder().user(user).name("Reserva de Emergência").institution("Nubank")
                .targetAmount(BigDecimal.valueOf(10000)).yieldType(YieldType.CDI).build();
        Goal goalTrip = Goal.builder().user(user).name("Viagem de Férias").institution("Itaú")
                .targetAmount(BigDecimal.valueOf(3000)).yieldType(YieldType.SELIC).build();

        BigDecimal emergencyTotal = BigDecimal.ZERO;
        BigDecimal emergencyYield = BigDecimal.ZERO;
        BigDecimal tripTotal = BigDecimal.ZERO;
        BigDecimal tripYield = BigDecimal.ZERO;

        Random random = new Random();
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            int year = monthDate.getYear();
            int month = monthDate.getMonthValue();

            LocalDate salaryDate = LocalDate.of(year, month, 5);
            transactions.add(Transaction.builder().user(user).account(itau).category(catSalary)
                    .amount(BigDecimal.valueOf(4800.00)).type(TransactionType.INCOME).description("Salário Mensal")
                    .transactionDate(salaryDate).isPaid(salaryDate.isBefore(today) || salaryDate.isEqual(today))
                    .isFixed(true).build());

            LocalDate rentDate = LocalDate.of(year, month, 10);
            transactions.add(Transaction.builder().user(user).account(itau).category(catHousing)
                    .amount(BigDecimal.valueOf(1500.00)).type(TransactionType.EXPENSE).description("Aluguel")
                    .transactionDate(rentDate).isPaid(rentDate.isBefore(today) || rentDate.isEqual(today))
                    .isFixed(true).build());

            LocalDate lightDate = LocalDate.of(year, month, 12);
            transactions.add(Transaction.builder().user(user).account(itau).category(catHousing)
                    .amount(randomAmount(120, 180, random)).type(TransactionType.EXPENSE).description("Conta de Luz")
                    .transactionDate(lightDate).isPaid(lightDate.isBefore(today) || lightDate.isEqual(today))
                    .isFixed(false).build());

            LocalDate market1 = LocalDate.of(year, month, 8);
            transactions.add(Transaction.builder().user(user).account(nubank).category(catFood)
                    .amount(randomAmount(300, 450, random)).type(TransactionType.EXPENSE).description("Supermercado")
                    .transactionDate(market1).isPaid(market1.isBefore(today) || market1.isEqual(today))
                    .isFixed(false).build());

            LocalDate market2 = LocalDate.of(year, month, 22);
            if (market2.isBefore(today) || market2.isEqual(today)) {
                transactions.add(Transaction.builder().user(user).account(nubank).category(catFood)
                        .amount(randomAmount(200, 350, random)).type(TransactionType.EXPENSE).description("Supermercado")
                        .transactionDate(market2).isPaid(true).isFixed(false).build());
            }

            LocalDate fuelDate = LocalDate.of(year, month, 15);
            transactions.add(Transaction.builder().user(user).account(nubank).creditCard(ccNubank).category(catTransport)
                    .amount(randomAmount(150, 200, random)).type(TransactionType.EXPENSE).description("Posto Ipiranga")
                    .transactionDate(fuelDate).isPaid(i > 0).isFixed(false).build());

            LocalDate uberDate = LocalDate.of(year, month, 20);
            transactions.add(Transaction.builder().user(user).account(nubank).creditCard(ccNubank).category(catTransport)
                    .amount(randomAmount(30, 60, random)).type(TransactionType.EXPENSE).description("Uber")
                    .transactionDate(uberDate).isPaid(i > 0).isFixed(false).build());

            LocalDate leisureDate = LocalDate.of(year, month, 18);
            transactions.add(Transaction.builder().user(user).account(nubank).creditCard(ccNubank).category(catLeisure)
                    .amount(randomAmount(80, 150, random)).type(TransactionType.EXPENSE).description("Restaurante")
                    .transactionDate(leisureDate).isPaid(i > 0).isFixed(false).build());

            LocalDate pharmacyDate = LocalDate.of(year, month, 2);
            transactions.add(Transaction.builder().user(user).account(nubank).creditCard(ccNubank).category(catHealth)
                    .amount(randomAmount(40, 90, random)).type(TransactionType.EXPENSE).description("Farmácia")
                    .transactionDate(pharmacyDate).isPaid(i > 0).isFixed(false).build());

            BigDecimal emergencyDep = i == 5 ? BigDecimal.valueOf(200) : randomAmount(250, 400, random);
            BigDecimal emergencyYld = randomAmount(5, 12, random);
            emergencyTotal = emergencyTotal.add(emergencyDep);
            emergencyYield = emergencyYield.add(emergencyYld);

            LocalDate invest1 = LocalDate.of(year, month, 6);
            transactions.add(Transaction.builder().user(user).account(itau).category(catInvest)
                    .amount(emergencyDep).type(TransactionType.EXPENSE).description("Guarda - Reserva")
                    .transactionDate(invest1).isPaid(invest1.isBefore(today) || invest1.isEqual(today))
                    .isFixed(false).build());

            BigDecimal tripDep = randomAmount(100, 200, random);
            BigDecimal tripYld = randomAmount(2, 6, random);
            tripTotal = tripTotal.add(tripDep);
            tripYield = tripYield.add(tripYld);

            LocalDate invest2 = LocalDate.of(year, month, 7);
            transactions.add(Transaction.builder().user(user).account(nubank).category(catInvest)
                    .amount(tripDep).type(TransactionType.EXPENSE).description("Guarda - Viagem")
                    .transactionDate(invest2).isPaid(invest2.isBefore(today) || invest2.isEqual(today))
                    .isFixed(false).build());
        }

        transactionRepository.saveAll(transactions);

        goalEmergency.setCurrentAmount(emergencyTotal.add(emergencyYield));
        goalEmergency.setYieldAmount(emergencyYield);
        goalRepository.save(goalEmergency);

        goalTrip.setCurrentAmount(tripTotal.add(tripYield));
        goalTrip.setYieldAmount(tripYield);
        goalRepository.save(goalTrip);
    }

    private BigDecimal randomAmount(double min, double max, Random random) {
        double val = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}