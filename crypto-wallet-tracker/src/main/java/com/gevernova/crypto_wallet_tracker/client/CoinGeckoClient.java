package com.gevernova.crypto_wallet_tracker.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class CoinGeckoClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Map<String, Number>> getPricesInInr(String[] coinIds) {
        String ids = String.join(",", coinIds);
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + ids + "&vs_currencies=inr";

        log.info("Calling CoinGecko API: {}", url);
        return restTemplate.getForObject(url, Map.class);
    }
}