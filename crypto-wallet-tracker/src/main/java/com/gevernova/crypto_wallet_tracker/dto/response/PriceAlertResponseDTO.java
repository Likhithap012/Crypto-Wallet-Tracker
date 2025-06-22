package com.gevernova.crypto_wallet_tracker.dto.response;

// PriceAlertRequest
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PriceAlertResponseDTO {
    private String coinSymbol;
    private Double targetPrice;
    private Boolean isGreaterThan;
    private Boolean triggered;
    private LocalDateTime createdAt;
}