package com.gevernova.crypto_wallet_tracker.repository;

import com.gevernova.crypto_wallet_tracker.entity.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    List<PriceAlert> findByEmail(String email);
    List<PriceAlert> findByTriggeredFalse();
}