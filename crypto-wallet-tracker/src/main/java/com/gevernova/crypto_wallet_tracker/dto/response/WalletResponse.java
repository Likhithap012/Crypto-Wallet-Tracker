package com.gevernova.crypto_wallet_tracker.dto.response;
// WalletRequest
import lombok.Builder;
import lombok.Data;

// 	WalletRequest
import java.time.LocalDateTime;

@Data
@Builder
public class WalletResponse {
    private String coinSymbol;
    private Double units;
    private Double buyPrice;
    private Double currentMarketPrice;
    private Double currentValue;
    private Double gainLoss;
    private Double gainLossPercent;
    private LocalDateTime addedOn;
}