package com.gevernova.crypto_wallet_tracker.dto.response;
// WalletRequest
import lombok.Builder;
import lombok.Data;

// 	WalletRequest
import java.time.LocalDateTime;

@Data
@Builder
public class WalletResponseDTO {
    private String coin;
    private Double units;
    private Double buyPrice;
}