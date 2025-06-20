package com.gevernova.crypto_wallet_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique entry ID

    @ManyToOne
    private User user;  // Owner of the wallet entry

    @Column(nullable = false)
    private String coinSymbol;  // e.g., BTC, ETH

    private Double units;  // Number of coins held

    private Double buyPrice;  // Buy price per unit

    private LocalDateTime addedOn = LocalDateTime.now();  // Time of adding the entry
}
