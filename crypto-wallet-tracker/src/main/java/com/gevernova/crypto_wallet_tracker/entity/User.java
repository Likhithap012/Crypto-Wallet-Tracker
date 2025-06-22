package com.gevernova.crypto_wallet_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique user ID

    @Column(nullable = false)
    private String name;  // User's full name

    @Column(unique = true, nullable = false)
    private String email;  // Must be unique

    @Column(nullable = false)
    private String password;  // Encrypted password

    @Enumerated(EnumType.STRING)
    private Role role;  // USER or ADMIN

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WalletEntry> wallet;  // User's crypto holdings

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PriceAlert> alerts;  // User's active price alerts
}