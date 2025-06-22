package com.gevernova.crypto_wallet_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String coin;

    private Double units;

    private Double buyPrice;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}