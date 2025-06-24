package com.gevernova.crypto_wallet_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String coin;
    private double targetPrice;
    @Column(name = "alert_condition")
    private String condition;
    private boolean triggered;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}