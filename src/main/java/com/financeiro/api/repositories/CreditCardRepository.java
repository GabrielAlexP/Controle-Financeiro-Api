package com.financeiro.api.repositories;

import com.financeiro.api.models.CreditCard;
import com.financeiro.api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    @Query("SELECT c FROM CreditCard c WHERE c.account.user = :user")
    List<CreditCard> findAllByUser(@Param("user") User user);

    @Query("SELECT c FROM CreditCard c WHERE c.id = :id AND c.account.user = :user")
    Optional<CreditCard> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
}