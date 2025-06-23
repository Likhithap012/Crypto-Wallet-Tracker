package com.gevernova.crypto_wallet_tracker.repository;

import com.gevernova.crypto_wallet_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Used in register() to check if email already exists
    boolean existsByEmail(String email);

    // Used in login() to fetch user by email
    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);
}