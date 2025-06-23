package com.gevernova.crypto_wallet_tracker.mapper;

import com.gevernova.crypto_wallet_tracker.dto.response.CoinPriceResponseDTO;
import com.gevernova.crypto_wallet_tracker.entity.CoinPrice;
import org.springframework.stereotype.Component;

@Component
public class CoinPriceMapper {
    public CoinPriceResponseDTO makeCoinPriceResponseDTO(CoinPrice coinPrice) {
        return CoinPriceResponseDTO.builder()
                .coinSymbol(coinPrice.getCoinSymbol())
                .currentPrice(coinPrice.getCurrentPrice())
                .lastUpdated(coinPrice.getLastUpdated())
                .build();
    }
}
