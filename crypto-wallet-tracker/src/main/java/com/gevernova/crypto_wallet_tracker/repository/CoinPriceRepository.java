package com.gevernova.crypto_wallet_tracker.repository;

import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoinPriceRepository extends JpaRepository<CoinPrice, Long> {
    Optional<CoinPrice> findByCoinSymbol(String coinSymbol);
}
