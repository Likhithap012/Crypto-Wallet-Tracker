package com.gevernova.crypto_wallet_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import com.gevernova.crypto_wallet_tracker.entity.User;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique alert ID

    @ManyToOne
    private User user;  // Alert belongs to a specific user

    @Column(nullable = false)
    private String coinSymbol;  // Coin symbol (e.g., BTC, ETH)

    @Column(nullable = false)
    private Double targetPrice;  // Price to watch for

    @Column(nullable = false)
    private boolean isGreaterThan;  // true = alert if price > target

    private boolean triggered = false;  // Mark if alert condition is met

    private LocalDateTime createdAt = LocalDateTime.now();  // Alert creation time
}

