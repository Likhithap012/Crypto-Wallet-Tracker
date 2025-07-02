package com.gevernova.crypto_wallet_tracker.repository;

import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoinPriceRepositoryTest {

    @Autowired
    private CoinPriceRepository coinPriceRepository;


    @Test
    @Order(2)
    @Rollback
    void testFindByCoinSymbol_Negative() {
        Optional<CoinPrice> result = coinPriceRepository.findByCoinSymbol("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    @Order(4)
    @Rollback
    void testFindPriceByCoinSymbol_Negative() {
        Optional<Double> price = coinPriceRepository.findPriceByCoinSymbol("unknown");
        assertThat(price).isEmpty();
    }
}
