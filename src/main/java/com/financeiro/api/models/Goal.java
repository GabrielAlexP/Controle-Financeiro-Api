package com.financeiro.api.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "goals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "target_amount", precision = 10, scale = 2)
    private BigDecimal targetAmount;

    @Builder.Default
    @Column(name = "current_amount", precision = 10, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "yields_cdi", nullable = false)
    private Boolean yieldsCdi = true;
}