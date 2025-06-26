package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import com.gevernova.crypto_wallet_tracker.mapper.SummaryMapper;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import com.gevernova.crypto_wallet_tracker.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryService implements SummaryServiceInterface {

    private final WalletRepository walletRepository;
    private final CoinPriceRepository coinPriceRepository;
    private final SummaryMapper summaryMapper;

    @Override
    public SummaryResponseDTO getSummaryForUser(String email) {
        List<WalletEntry> holdings = walletRepository.findByUserEmail(email);

        double totalInvestment = 0.0;
        double currentValue = 0.0;

        for (WalletEntry entry : holdings) {
            double units = entry.getUnits();
            double buyPrice = entry.getBuyPrice();
            double invested = units * buyPrice;
            totalInvestment += invested;

            String coinSymbol = entry.getCoin();
            if (coinSymbol != null) {
                Double marketValue = coinPriceRepository.findByCoinSymbol(coinSymbol.toLowerCase())
                        .map(price -> {
                            double value = price.getCurrentPrice() * units;
                            log.info("Market value for {}: {} * {} = {}", coinSymbol, price.getCurrentPrice(), units, value);
                            return value;
                        })
                        .orElseGet(() -> {
                            log.warn("No market price found for coin: {}", coinSymbol);
                            return 0.0;
                        });

                currentValue += marketValue;
            } else {
                log.warn("Wallet entry has null coin for user {}", email);
            }
        }

        log.info("Summary for {}: Total Investment = {}, Current Value = {}", email, totalInvestment, currentValue);
        return summaryMapper.toDTO(totalInvestment, currentValue);
    }
}
