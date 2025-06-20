package com.gevernova.crypto_wallet_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Auto-generated primary key

    @Column(nullable = false, unique = true)
    private String coinSymbol;  // e.g., BTC, ETH

    private Double currentPrice;  // Latest market price

    private LocalDateTime lastUpdated;  // Timestamp of last update
}
