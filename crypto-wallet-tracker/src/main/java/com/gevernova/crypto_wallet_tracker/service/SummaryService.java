package com.gevernova.crypto_wallet_tracker.service;

import com.gevernova.crypto_wallet_tracker.dto.response.SummaryResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.WalletEntry;
import com.gevernova.crypto_wallet_tracker.mapper.SummaryMapper;
import com.gevernova.crypto_wallet_tracker.repository.CoinPriceRepository;
import com.gevernova.crypto_wallet_tracker.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService implements ISummaryService {

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

            Double marketValue = coinPriceRepository.findByCoinSymbol(entry.getCoin().toLowerCase())
                    .map(price -> price.getCurrentPrice() * units)
                    .orElse(0.0);

            currentValue += marketValue;
        }

        return summaryMapper.toDTO(totalInvestment, currentValue);
    }

}
