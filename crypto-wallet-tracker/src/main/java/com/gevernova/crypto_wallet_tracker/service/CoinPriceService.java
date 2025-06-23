package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.client.CoinGeckoClient;
import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinPriceService implements ICoinPriceService {

    private final CoinPriceRepository coinPriceRepository;
    private final CoinGeckoClient coinGeckoClient;

    private final String[] coinIds = {
            "bitcoin", "ethereum", "cardano", "solana",
            "polkadot", "dogecoin", "chainlink", "uniswap",
            "shiba-inu", "polygon"
    };

    public void fetchAndSave() {
        log.info("Fetching crypto prices in INR from client...");

        Map<String, Map<String, Number>> response = coinGeckoClient.getPricesInInr(coinIds);

        if (response != null) {
            for (String coinId : coinIds) {
                Map<String, Number> coinData = response.get(coinId);
                if (coinData != null && coinData.get("inr") != null) {
                    Double price = coinData.get("inr").doubleValue();

                    log.info("Fetched INR price for {}: {}", coinId, price);

                    coinPriceRepository.findByCoinSymbol(coinId)
                            .ifPresentOrElse(
                                    existing -> {
                                        existing.setCurrentPrice(price);
                                        existing.setLastUpdated(LocalDateTime.now());
                                        coinPriceRepository.save(existing);
                                        log.info("Updated record for {}", coinId);
                                    },
                                    () -> {
                                        CoinPrice newCoin = CoinPrice.builder()
                                                .coinSymbol(coinId)
                                                .currentPrice(price)
                                                .lastUpdated(LocalDateTime.now())
                                                .build();
                                        coinPriceRepository.save(newCoin);
                                        log.info("Inserted new record for {}", coinId);
                                    }
                            );
                } else {
                    log.warn("No INR data available for {}", coinId);
                }
            }
        } else {
            log.error("Response from CoinGeckoClient is null");
        }
    }
    public List<CoinPrice> getAllCoinPrices() {
        return coinPriceRepository.findAll();
    }
}