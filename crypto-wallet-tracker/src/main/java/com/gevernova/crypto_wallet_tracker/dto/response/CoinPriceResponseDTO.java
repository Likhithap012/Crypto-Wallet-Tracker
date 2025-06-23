package com.gevernova.crypto_wallet_tracker.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinPriceResponseDTO {
    private String coinSymbol;
    private Double currentPrice;
    private LocalDateTime lastUpdated;
}