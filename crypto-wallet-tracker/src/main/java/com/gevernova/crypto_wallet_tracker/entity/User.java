package com.gevernova.crypto_wallet_tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_verified")
    private boolean verified = false;  //Required for email verification

    @Column(name = "otp")
    private String otp;  //Required for OTP login

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WalletEntry> wallet;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PriceAlert> alerts;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;
}

