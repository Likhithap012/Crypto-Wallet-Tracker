package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinPriceService {

    private final CoinPriceRepository coinPriceRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final String[] coinIds = {
            "bitcoin", "ethereum", "cardano", "solana",
            "polkadot", "dogecoin", "chainlink", "uniswap",
            "shiba-inu", "polygon"
    };

    public void fetchAndSave() {
        log.info("Fetching crypto prices in INR...");

        String ids = String.join(",", coinIds);
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + ids + "&vs_currencies=inr";

        Map<String, Map<String, Number>> response = restTemplate.getForObject(url, Map.class);

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
            log.error("API response from CoinGecko is null");
        }
    }
}
