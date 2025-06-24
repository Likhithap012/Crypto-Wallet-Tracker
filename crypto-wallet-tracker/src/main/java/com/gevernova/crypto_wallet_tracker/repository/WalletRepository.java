package com.gevernova.crypto_wallet_tracker.repository;

import com.gevernova.crypto_wallet_tracker.entity.User;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<WalletEntry, Long> {
    List<WalletEntry> findByUser(User user);
    List<WalletEntry> findByUserEmail(String email);
}

